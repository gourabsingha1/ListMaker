package com.example.listmaker.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.listmaker.R
import com.example.listmaker.adapter.ItemAdapter
import com.example.listmaker.databinding.ActivityItemListBinding
import com.example.listmaker.fragment.AddItemListFragment
import com.example.listmaker.fragment.EditItemListFragment
import com.example.listmaker.viewmodel.ItemListTrashViewModel
import com.example.listmaker.viewmodel.ItemListViewModel

class ItemListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemListBinding
    private lateinit var itemListViewModel: ItemListViewModel
    private lateinit var itemListTrashViewModel: ItemListTrashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back press
        binding.ivItemListBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
        }

        // Get position to get currentItemListWithItems
        val itemListId = intent.getLongExtra("EXTRA_ITEM_LIST_ID", 0)

        // Set ViewModel
        itemListTrashViewModel = ViewModelProvider(this)[ItemListTrashViewModel::class.java]
        itemListViewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        itemListViewModel.itemListsWithItems.observe(this) { itemListsWithItems ->
            // Get currentItemListWithItems
            for (currentItemListWithItems in itemListsWithItems) {
                if (currentItemListWithItems.itemList?.id == itemListId) {
                    // Set ItemList and Items
                    binding.apply {
                        tvItemListName.text = currentItemListWithItems.itemList.name
                        rvItem.apply {
                            adapter = ItemAdapter(
                                currentItemListWithItems.items!!,
                                onUpdate = { item ->
                                    itemListViewModel.upsertItem(item)
                                },
                                onDelete = { item ->
                                    itemListViewModel.deleteItem(item)
                                }
                            )
                        }

                        // Edit ItemList Name
                        tvItemListName.setOnClickListener {
                            val dialog = EditItemListFragment(currentItemListWithItems.itemList)
                            dialog.show(supportFragmentManager, "EditItemListDialogFragment")
                        }

                        // Add Item
                        ivItemListAddItem.setOnClickListener {
                            val dialog = AddItemListFragment(currentItemListWithItems.itemList)
                            dialog.show(supportFragmentManager, "AddItemListDialogFragment")
                        }

                        // Delete ItemList
                        ivItemListDelete.setOnClickListener {// Move to Trash
                            itemListTrashViewModel.upsertItemList(currentItemListWithItems.itemList!!)
                            currentItemListWithItems.items?.forEach { item ->
                                itemListTrashViewModel.upsertItem(item)
                            }
                            itemListViewModel.deleteItemList(currentItemListWithItems.itemList)
                            startActivity(Intent(this@ItemListActivity, HomeActivity::class.java))
                            finish()
                        }
                    }
                    break
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom)
    }
}