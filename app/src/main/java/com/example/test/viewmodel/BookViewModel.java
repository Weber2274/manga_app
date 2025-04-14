package com.example.test.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test.model.Book;
import com.example.test.model.BookRepository;

import java.util.List;

public class BookViewModel extends ViewModel {
    private MutableLiveData<List<Book>> books = new MutableLiveData<>();
    private BookRepository repository = new BookRepository();

    public LiveData<List<Book>> getBooks() {
        return books;
    }

    public void loadBooks(String email, String password) {
        repository.fetchBooksFromUpdatePage(new BookRepository.OnBooksLoadedListener() {
            @Override
            public void onLoaded(List<Book> result) {
                books.postValue(result);
            }

            @Override
            public void onError(String message) {
                // 可以額外提供錯誤 LiveData 處理
            }
        });
    }
}
