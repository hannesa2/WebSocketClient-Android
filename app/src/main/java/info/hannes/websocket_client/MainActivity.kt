package info.hannes.websocket_client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import info.hannes.websocket_client.databinding.ActivityMainBinding
import timber.log.Timber
import java.net.URI
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var webSocketClient: ChatWebSocketClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.server_url)

        val btnSend = binding.btnSend
        val editMsg = binding.editMsg
        val listview1 = binding.listview1

        val serverUri = URI(getString(R.string.server_url))

        webSocketClient = ChatWebSocketClient(serverUri) { message ->

            runOnUiThread {
                run {
                    val incomming = HashMap<String, Any>()
                    incomming["message"] = "<-$message from ${getString(R.string.server_url)}"
                    (listview1.adapter as ChatAdapter).addItem(incomming)
                }
            }
        }
        listview1.adapter = ChatAdapter()
        webSocketClient.connect()


        btnSend.setOnClickListener {
            try {
                webSocketClient.sendMessage(editMsg.text.toString())
                editMsg.setText("")
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketClient.close()
    }


    class ChatAdapter : BaseAdapter() {

        private var dataList: MutableList<HashMap<String, Any>> = mutableListOf()

        fun addItem(item: HashMap<String, Any>) {
            dataList.add(item)
            if (dataList.size > 12)
                dataList.removeAt(0)
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return dataList.size
        }

        override fun getItem(index: Int): HashMap<String, Any> {
            return dataList[index]
        }

        override fun getItemId(index: Int): Long {
            return index.toLong()
        }

        override fun getView(position: Int, v: View?, container: ViewGroup?): View? {
            val inflater = LayoutInflater.from(container?.context)
            var view = v
            if (view == null) {
                view = inflater.inflate(R.layout.row, container, false)
            }

            val textMessage = view?.findViewById<TextView>(R.id.textMessage)

            if (textMessage != null) {
                textMessage.text = dataList[position]["message"].toString()
            }

            return view
        }

    }

}