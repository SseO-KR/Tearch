package org.kpu.taver

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.MapView
import org.kpu.taver.databinding.ItemCafelistBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val locationmanager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled: Boolean = locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean = locationmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var myLatitude : Double = 37.340824
        var myLongitude : Double = 126.731501
            //메니페스트에 권한이 추가되어 있지만 다시 한번 확인
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else{
            when{   //프로바이더 제공자 활성화 여부 체크
                isNetworkEnabled -> {
                    val location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    myLongitude = location?.longitude!!
                    myLatitude = location.latitude
                }
                isGPSEnabled->{
                    val location = locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    myLongitude = location?.longitude!!
                    myLatitude = location.latitude
                }
                else -> { }
            }
        }

        val leastLatitude = myLatitude - 0.04
        val leastLongitude = myLongitude - 0.04
        val maxLatitude = myLatitude + 0.04
        val maxLongitude = myLongitude + 0.04

        val firebaseDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
        val myRef : DatabaseReference = firebaseDatabase.getReference("매장")

        var cafelist = arrayListOf<CafeVO?>()


        myRef.orderByChild("latitude").startAt(leastLatitude).endAt(maxLatitude).addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    cafelist.clear()

                    for(sShot : DataSnapshot in snapshot.children){
                        val cafevo : CafeVO? = sShot.getValue<CafeVO>(CafeVO::class.java)
                        if(cafevo!!.longitude >= leastLongitude && cafevo!!.longitude <= maxLongitude){
                            cafelist.add(cafevo)
                        }
                    }

                    val maplistBtn = findViewById<ImageButton>(R.id.maplistBtn)
                    maplistBtn.setOnClickListener {
                        val intent : Intent = Intent(this@MainActivity, CafeMapListActivity::class.java)
                        intent.putExtra("CafeList", cafelist)
                        startActivity(intent)
                    }

                    //main_rcv.adapter = CafeAdapter(cafelist)
                    val myAdapter = CafeAdapter(cafelist)
                    main_rcv.adapter = myAdapter
                    main_rcv.layoutManager = LinearLayoutManager(this@MainActivity)
                    main_rcv.setHasFixedSize(true)




                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        search_view.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val filterdList = ArrayList<CafeVO?>()
                for(cafevo in cafelist){
                    if(cafevo!!.cafeName.toLowerCase().contains(query!!.toLowerCase())){
                        filterdList.add(cafevo)
                    }
                }

                val myAdapter = CafeAdapter(filterdList)
                main_rcv.adapter = myAdapter
                main_rcv.layoutManager = LinearLayoutManager(this@MainActivity)
                main_rcv.setHasFixedSize(true)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filterdList = ArrayList<CafeVO?>()
                for(cafevo in cafelist){
                    if(cafevo!!.cafeName.toLowerCase().contains(newText!!.toLowerCase())){
                        filterdList.add(cafevo)
                    }
                }

                val myAdapter = CafeAdapter(filterdList)
                main_rcv.adapter = myAdapter
                main_rcv.layoutManager = LinearLayoutManager(this@MainActivity)
                main_rcv.setHasFixedSize(true)

                return true
            }

        })

        imgBtnQuestion.setOnClickListener {
            val dlgView = layoutInflater.inflate(R.layout.my_dialog, null)
            val dlgBuilder = AlertDialog.Builder(this)
            dlgBuilder.setView(dlgView)
            dlgBuilder.setPositiveButton("확인"){ dlialogInterface, i->

            }.show()
        }
    }
    }


