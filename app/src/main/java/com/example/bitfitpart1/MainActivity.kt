package com.example.bitfitpart1

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val items = mutableListOf<Item>()
    lateinit var adapter: ItemAdapter

    val getItem = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val returnedIntent = result.data
            val newItem = returnedIntent?.getSerializableExtra("Result") as Item
            items.add(newItem)
            adapter.notifyItemInserted(items.size - 1)

            val newItemEntity = ItemEntity(newItem.itemName, newItem.calories)
            lifecycleScope.launch(Dispatchers.IO) {
                (application as ItemApplication).db.itemDao().insert(newItemEntity)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Lookup the RecyclerView in activity layout
        val itemsRv = findViewById<RecyclerView>(R.id.itemsRv)
        // Create adapter passing in the list of emails
        adapter = ItemAdapter(items)
        // Attach the adapter to the RecyclerView to populate items
        itemsRv.adapter = adapter

        //lifecycleScope.launch (Dispatchers.IO){
         //   (application as ItemApplication).db.itemDao().deleteAll()}


        lifecycleScope.launch {
            (application as ItemApplication).db.itemDao().getAll().collect { databaseList ->
                databaseList.map { entity ->
                    Item(
                        entity.itemName,
                        entity.calories
                    )
                }.also { mappedList ->
                    items.clear()
                    items.addAll(mappedList)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        // Set layout manager to position the items
        itemsRv.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.addButton).setOnClickListener {

            val intent = Intent(this, EnterActivity::class.java)
            getItem.launch(intent)

            /*
            val itemName = findViewById<EditText>(R.id.itemNameEditText).text.toString()
            val calories = findViewById<EditText>(R.id.caloriesEditText).text.toString()
            val newItem = Item(itemName, calories)
            // Add new emails to existing list of emails
            items.add(newItem)
            // Notify the adapter there's new emails so the RecyclerView layout is updated
            adapter.notifyItemInserted(items.size - 1)

            // Add the new item to database
            val newItemEntity = ItemEntity(itemName, calories)
            lifecycleScope.launch(Dispatchers.IO) {
                (application as ItemApplication).db.itemDao().insert(newItemEntity)
            }

            itemsRv.scrollToPosition(items.size - 1)
            findViewById<EditText>(R.id.itemNameEditText).text.clear()
            findViewById<EditText>(R.id.caloriesEditText).text.clear()
            */

        }

        adapter.setOnItemLongClickListener(object : ItemAdapter.OnItemLongClickListener {
            override fun onItemLongClick(itemView: View?, position: Int) {
                val itemName = items[position].itemName
                items.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(applicationContext, "$itemName Deleted", Toast.LENGTH_LONG).show()
            }
        })
    }

}

