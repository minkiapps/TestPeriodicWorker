package com.minkiapps.workmanagertester

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minkiapps.workmanagertester.databinding.ActivityMainBinding
import com.minkiapps.workmanagertester.di.workerQualifier
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val prefs: SharedPreferences by inject(workerQualifier)
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

        binding.btnActMainDelete.setOnClickListener {
            prefs.edit(commit = true) {
                clear()
            }
            refreshAdapter()
        }

        binding.swActMainBatteryOptimiser.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked)
                return@setOnCheckedChangeListener

            startActivityForResult(
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    .setData(Uri.parse("package:${packageName}")),
                REQUEST_BATTERY_OPTIMISATION_ENABLE_DIALOG
            )
        }

        refreshBatteryOptimisationSwitch()
        refreshAdapter()
    }

    private fun refreshBatteryOptimisationSwitch() {
        val isIgnored = isIgnoringBatteryOptimisation()
        binding.swActMainBatteryOptimiser.run {
            isChecked = isIgnored
            isEnabled = !isIgnored

            text = if (isIgnored) "Ignored by battery optimiser"
            else "Not ignored by battery optimiser"
        }
    }

    private fun isIgnoringBatteryOptimisation(): Boolean {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_BATTERY_OPTIMISATION_ENABLE_DIALOG -> {
                refreshBatteryOptimisationSwitch()
            }
        }
    }

    private fun refreshAdapter() {
        val records = prefs.all.toSortedMap().values.map { it.toString() }
        binding.rvActMain.adapter = RecordAdapter(records)
    }

    class RecordAdapter(private val records: List<String>) :
        RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

        // holder class to hold reference
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.tvLayListItem) as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = records[position]
        }

        override fun getItemCount(): Int {
            return records.size
        }
    }

    companion object {
        private const val REQUEST_BATTERY_OPTIMISATION_ENABLE_DIALOG = 10001
    }

}