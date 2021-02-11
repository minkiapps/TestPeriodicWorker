package com.minkiapps.workmanagertester

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minkiapps.workmanagertester.databinding.ActivityMainBinding
import com.minkiapps.workmanagertester.di.workerQualifier
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val prefs : SharedPreferences by inject(workerQualifier)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rvActMain.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        binding.btnActMainRefresh.setOnClickListener {
            refreshAdapter()
        }

        refreshAdapter()
    }

    private fun refreshAdapter() {
        val records = prefs.all.toSortedMap().values.map { it.toString() }
        binding.rvActMain.adapter = RecordAdapter(records)
    }

    class RecordAdapter(private val records: List<String>) : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

        // holder class to hold reference
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.tvLayListItem) as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = records[position]
        }

        override fun getItemCount(): Int {
            return records.size
        }
    }

}