package com.androidcamp.bookstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.androidcamp.bookstore.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.HashMap

class MainActivity : AppCompatActivity() , BooksAdapter.OnClick{
    private val TAG = "MainActivity"
    private val db = Firebase.firestore
    private val rtdb = Firebase.database
    private lateinit var adapter : BooksAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e(TAG, "onCreate: ", )
        adapter = BooksAdapter(this, this)
        binding.recBooks.adapter = adapter

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.e(TAG, "Fetching FCM registration token $token")
        })
    }

    override fun onResume() {
        super.onResume()

//        getFirestoreData()
        getRealtime()

        binding.btnAddBook.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))
        }
    }

    private fun getRealtime(){
        rtdb.reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = ArrayList<Book>()

                for (book in snapshot.children) {
                    val name  = book.child("name").value
                    val authorName = book.child("authorName").value
                    val imageUrl = book.child("imageUrl").value
                    val price = book.child("price").value
                    val rate = book.child("rate").value
                    val realizeDate = book.child("realizeDate").value
                    val b = Book(book.key!!, name.toString(),authorName.toString(),realizeDate.toString(),rate.toString().toLong(),price.toString().toLong(),imageUrl.toString())
                    books.add(b)
                    adapter.setData(books)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Can't Get Books Now. ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFirestoreData(){
        db.collection("Books").addSnapshotListener { value, error ->
            if (error != null){
                Toast.makeText(this, "Can't get data, ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "error: $error")
                return@addSnapshotListener
            }

            val books = ArrayList<Book>()
            value?.documents?.forEach {
//                Log.e(TAG, "onResume: ${it}")
                books.add(Book(
                    it.id,
                    it["name"].toString(),
                    it["authorName"].toString(),
//                    it["realizeDate"] as Timestamp,
                    it["rate"].toString().toLong(),
                    it["price"].toString().toLong(),
                    it["imageUrl"].toString()
                ))
            }
            adapter.setData(books)
        }
    }

    override fun onClickBook(itemId: Int) {
//        FCMService.sendRemoteNotification("test","abc abc")
    }

    override fun onClickEditBook(itemId: Int, book: Book) {
        val i = Intent(this, EditBookActivity::class.java)
        i.putExtra("id", book.id)
        startActivity(i)
    }
}