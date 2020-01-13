// BookController.aidl
package com.example.bookservice;

import com.example.bookservice.Book;

// Declare any non-default types here with import statements

interface BookController {
     List<Book> getBookList();

        void addBookInOut(inout Book book);
}
