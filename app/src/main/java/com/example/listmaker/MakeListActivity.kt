package com.example.listmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listmaker.adapter.ItemAdapter
import com.example.listmaker.adapter.ItemListAdapter
import com.example.listmaker.databinding.ActivityMakeListBinding
import com.example.listmaker.model.Item
import com.example.listmaker.model.ItemListWithItems
import com.example.listmaker.viewmodel.ItemListViewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.search.SearchBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MakeListActivity : AppCompatActivity(), ItemListAdapter.ItemListClickUpdateInterface {
    private lateinit var binding: ActivityMakeListBinding
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemListAdapter: ItemListAdapter
    private lateinit var itemListViewModel: ItemListViewModel
    private val itemsFromAI = arrayListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back press
        binding.ivMakeListBack.setOnClickListener {
            finish()
        }

        // Initialize Adapter
        itemAdapter = ItemAdapter(
            mutableListOf(),
            onDelete = {item ->
                itemListViewModel.deleteItem(item)
            },
            onUpdate = { item ->
                itemListViewModel.upsertItem(item)
            })

        itemListAdapter = ItemListAdapter(
            this,
            onUpdateItemList = { itemList ->
                itemListViewModel.upsertItemList(itemList)
            },
            onUpdateItem = { item ->
                itemListViewModel.upsertItem(item)
            },
            onDeleteItemList = { itemListWithItems ->
                itemListViewModel.deleteItemList(itemListWithItems.itemList!!)
            },
            onDeleteItem = { item ->
                itemListViewModel.deleteItem(item)
            },
            showMenuDeleteSelected = { show ->
                showMenuDeleteSelected(show)
            })

        // RecyclerView
        val rvMakeList = findViewById<RecyclerView>(R.id.rvMakeList)
        rvMakeList.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(this@MakeListActivity)
        }

        // ViewModel
        itemListViewModel = ViewModelProvider(this)[ItemListViewModel::class.java]
        itemListViewModel.itemListsWithItems.observe(this) { itemListsWithItems ->
            itemListAdapter.updateItemListsWithItems(itemListsWithItems)
        }

        // Create list
        binding.btnCreate.setOnClickListener {
            it.hideKeyboard()
            binding.pbSearch.visibility = View.VISIBLE

            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-pro",
                    apiKey = "AIzaSyBlRf7lSAPt887BJav5UuwmJNW4jhTTN4Q"
                )
                CoroutineScope(Dispatchers.IO).launch {
                    val sliderVal = binding.sliderMakeList.value
                    val itemListName = binding.etListName.text.toString().trim()
                    val searchQuery = "Maximum $sliderVal items of $itemListName. I just want the items in a single line separated by semi colon."
                    val response = generativeModel.generateContent(searchQuery)
                    withContext(Dispatchers.Main) {
                        // Add AI generated items in the array
                        extractItems(response.text!!)

                        // UI Changes
                        binding.pbSearch.visibility = View.INVISIBLE
                        binding.btnAddList.visibility = View.VISIBLE
                        binding.btnCreate.text = "Recreate"
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()
                binding.pbSearch.visibility = View.INVISIBLE
            }
        }

        // Add the list
        binding.btnAddList.setOnClickListener {
            val itemListName = binding.etListName.text.toString().trim()
            itemListViewModel.insertItemListWithItems(itemListName, itemsFromAI)
            Toast.makeText(this, "List added!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun extractItems(text: String) {
        itemsFromAI.clear()
        val res = mutableSetOf<String>()
        val n = text.length
        var i = 0
        while (i < n) {
            var name = ""
            while (i < n && text[i] != ';') {
                name += text[i++]
            }
            res.add(name.trim())
            i++
        }
        res.forEach { name ->
            itemsFromAI.add(Item(name = name))
        }
        itemAdapter.updateItems(itemsFromAI)
    }

    // Show delete menu
    private fun showMenuDeleteSelected(show: Boolean) {
        findViewById<SearchBar>(R.id.sbHomeSearch).menu.findItem(R.id.menuHomeDeleteSelected).isVisible = show
    }

    override fun onItemListClick(currentItemListWithItems: ItemListWithItems) {

    }
}