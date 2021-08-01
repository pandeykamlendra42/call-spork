package com.custom.managecalls

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.custom.managecalls.adapters.ManageCallsAdapter
import com.custom.managecalls.fragments.FakeEntryDialogFragment
import kotlinx.android.synthetic.main.activity_manage_fake_calls.*

class ManageFakeCallsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_fake_calls)

        rvFakeCalls.layoutManager = LinearLayoutManager(this)
        rvFakeCalls.adapter = ManageCallsAdapter(this, ArrayList())
        fabCreateFakeCall.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            val prev = supportFragmentManager.findFragmentByTag("dialog")
            if (prev != null) {
                fragmentTransaction.remove(prev)
            }
            fragmentTransaction.addToBackStack(null)
            val dialogFragment = FakeEntryDialogFragment()
            dialogFragment.show(fragmentTransaction, "dialog")
        }

    }
}
