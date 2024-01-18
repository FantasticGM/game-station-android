package com.skyx.berrygame

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.skyx.berrygame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
        updateHandler.sendEmptyMessageDelayed(66, 2000);
    }

    //设置返回键的监听
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode==KeyEvent.KEYCODE_BACK){
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show()
//                exitTime = System.currentTimeMillis()
//            } else {
//                finish()
//                System.exit(0)
//            }
//        }
//        return false
//    }

    private val updateHandler: Handler = object : Handler() {
        override fun dispatchMessage(msg: Message) {
            super.dispatchMessage(msg)
            if (msg.what === 66) {
                val intent = Intent(applicationContext, WebActivity::class.java)
                intent.putExtra("webUrl", "")
                startActivity(intent)
            }
        }
    }
}