package com.androidcamp.bookstore

class Book{
    @com.google.firebase.firestore.Exclude
    @com.google.firebase.database.Exclude
    var id: String? = null

    var name: String? = null
    var authorName: String? = null
    var realizeDate: String? = null
//    var realizeDate: com.google.firebase.Timestamp? = null
    var rate: Long? = null
    var price: Long? = null
    var imageUrl: String? = null

    constructor()

    constructor(id: String, name: String, authorName: String, realizeDate: String, rate: Long, price: Long, imageUrl: String){
        this.id = id
        this.name = name
        this.authorName = authorName
        this.realizeDate = realizeDate
        this.rate = rate
        this.price = price
        this.imageUrl = imageUrl
    }

    constructor(name: String, authorName: String, realizeDate: String, rate: Long, price: Long, imageUrl: String){
        this.name = name
        this.authorName = authorName
        this.realizeDate = realizeDate
        this.rate = rate
        this.price = price
        this.imageUrl = imageUrl
    }
}