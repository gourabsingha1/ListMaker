package com.example.listmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listmaker.adapter.ItemListTrashAdapter
import com.example.listmaker.databinding.ActivityTrashBinding
import com.example.listmaker.fragment.DeleteForeverFragment
import com.example.listmaker.model.ItemList
import com.example.listmaker.model.ItemListWithItems
import com.example.listmaker.viewmodel.ItemListTrashViewModel
import com.example.listmaker.viewmodel.ItemListViewModel
import com.google.android.material.navigation.NavigationView

class TrashActivity : AppCompatActivity(), ItemListTrashAdapter.ItemListTrashInterface {
    private lateinit var binding: ActivityTrashBinding
    private lateinit var itemListTrashAdapter: ItemListTrashAdapter
    private lateinit var itemListTrashViewModel: ItemListTrashViewModel
    private lateinit var itemListViewModel: ItemListViewModel
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navDrawer: NavigationView
    private var actionMode: ActionMode? = null
    private var selectAllToggle: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set toolbar
        setSupportActionBar(binding.toolbarTrash)

        // Initialize Adapter
        itemListTrashAdapter = ItemListTrashAdapter(this)

        // RecyclerView
        val rvLayoutManager = binding.rvTrash.layoutManager as LinearLayoutManager?
        val position =
            rvLayoutManager?.findFirstVisibleItemPosition() // So that the screen doesn't scroll up, everytime i delete something.
        binding.rvTrash.apply {
            adapter = itemListTrashAdapter
            layoutManager = LinearLayoutManager(this@TrashActivity)
        }
        rvLayoutManager?.scrollToPosition(position ?: 0)

        // ViewModel
        itemListViewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        itemListTrashViewModel = ViewModelProvider(this)[ItemListTrashViewModel::class.java]
        itemListTrashViewModel.itemListsWithItems.observe(this) { itemListsWithItems ->
            itemListTrashAdapter.updateItemListsWithItems(itemListsWithItems)

            // Show No ItemList Icon in Trash
            if (itemListsWithItems.isEmpty()) {
                binding.ivNoListInTrash.visibility = View.VISIBLE
                binding.tvNoItemListTrash.visibility = View.VISIBLE
            } else {
                binding.ivNoListInTrash.visibility = View.GONE
                binding.tvNoItemListTrash.visibility = View.GONE
            }
        }

        // Navigation Drawer
        navigationDrawer()
    }

    // Navigation Drawer
    private fun navigationDrawer() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayoutTrash)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        // when toggle is opened and back is pressed, the navigation bar will close
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navDrawer = findViewById(R.id.navDrawer)
        navDrawer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navTrash -> startActivity(Intent(this, TrashActivity::class.java))
                R.id.navLists -> startActivity(Intent(this, HomeActivity::class.java))
            }
            this.finish()
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Navigation Drawer closes onBackPressed
    override fun onBackPressed() {
        if (binding.drawerLayoutTrash.isDrawerOpen(navDrawer)) {
            binding.drawerLayoutTrash.closeDrawer(navDrawer)
        } else {
            super.onBackPressed()
        }
    }

    // Implement actionMode callback
    private val actionModeCallBack = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            binding.toolbarTrash.visibility = View.GONE
            menuInflater.inflate(R.menu.multiple_select_menu_trash, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                // Select All ItemLists
                R.id.menuMultipleSelectTrashSelectAll -> {
                    itemListTrashViewModel.itemListsWithItems.observe(this@TrashActivity) { itemListWithItems ->
                        itemListWithItems.forEach { currentItemListWithItems ->
                            currentItemListWithItems.itemList?.selected = selectAllToggle
                        }
                        if (selectAllToggle) {
                            actionMode?.title = "${itemListWithItems.size}"
                        } else {
                            actionMode?.finish()
                        }
                        selectAllToggle = !selectAllToggle
                        itemListTrashAdapter.notifyDataSetChanged()
                    }
                    return true
                }

                // Restore ItemList
                R.id.menuMultipleSelectTrashRestore -> {
                    itemListTrashViewModel.itemListsWithItems.observe(this@TrashActivity) { itemListWithItems ->
                        itemListWithItems.forEach { currentItemListWithItems ->
                            if (currentItemListWithItems.itemList?.selected == true) {
                                // Move to HomeActivity
                                currentItemListWithItems.itemList.selected = false
                                itemListViewModel.upsertItemList(currentItemListWithItems.itemList!!)
                                currentItemListWithItems.items?.forEach { item ->
                                    itemListViewModel.upsertItem(item)
                                }
                                // Delete from TrashActivity
                                itemListTrashViewModel.deleteItemList(currentItemListWithItems.itemList)
                                currentItemListWithItems.items?.forEach { item ->
                                    itemListTrashViewModel.deleteItem(item)
                                }
                            }
                        }
                        itemListTrashAdapter.notifyDataSetChanged()
                    }
                    actionMode?.finish()
                    return true
                }

                // Delete ItemList
                R.id.menuMultipleSelectTrashDelete -> {
                    val selectedLists = arrayListOf<ItemListWithItems>()
                    itemListTrashViewModel.itemListsWithItems.observe(this@TrashActivity) { itemListWithItems ->
                        itemListWithItems.forEach { currentItemListWithItems ->
                            if (currentItemListWithItems.itemList?.selected == true) {
                                selectedLists.add(currentItemListWithItems)
                            }
                        }
                        itemListTrashAdapter.notifyDataSetChanged()
                    }

                    // Delete from TrashActivity
                    val dialog = DeleteForeverFragment(selectedLists)
                    dialog.show(supportFragmentManager, "EditItemListDialogFragment")
                    actionMode?.finish()
                    return true
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            binding.toolbarTrash.visibility = View.VISIBLE
            itemListTrashViewModel.itemListsWithItems.observe(this@TrashActivity) { itemListWithItems ->
                itemListWithItems.forEach { currentItemListWithItems ->
                    currentItemListWithItems.itemList?.selected = false
                }
                itemListTrashAdapter.notifyDataSetChanged()
                actionMode = null
            }
        }
    }

    // Toggle selection
    private fun toggleSelection(itemList: ItemList) {
        itemList.selected = !itemList.selected
        itemListTrashAdapter.notifyDataSetChanged()

        // Show count of selected ItemLists
        var count = 0
        itemListTrashViewModel.itemListsWithItems.observe(this@TrashActivity) { itemListWithItems ->
            itemListWithItems.forEach { currentItemListWithItems ->
                if (currentItemListWithItems.itemList?.selected == true) {
                    count++
                }
            }
        }
        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = "$count"
        }
    }

    override fun onItemListClick(position: Int, itemList: ItemList) {
        if (actionMode != null) {
            toggleSelection(itemList)
        }
    }

    override fun onItemListLongPress(itemList: ItemList) {
        if (actionMode == null) {
            actionMode = startActionMode(actionModeCallBack)
        }
        toggleSelection(itemList)
    }

}

