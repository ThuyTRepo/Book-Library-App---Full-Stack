package com.example.library_app_back_end.service;

import com.example.library_app_back_end.constant.ErrorConstant;
import com.example.library_app_back_end.dao.BookRepository;
import com.example.library_app_back_end.dao.CheckoutRepository;
import com.example.library_app_back_end.dao.HistoryRepository;
import com.example.library_app_back_end.dao.PaymentRepository;
import com.example.library_app_back_end.dto.BookDTO;
import com.example.library_app_back_end.entity.Book;
import com.example.library_app_back_end.entity.Checkout;
import com.example.library_app_back_end.entity.History;
import com.example.library_app_back_end.entity.Payment;
import com.example.library_app_back_end.error.ValidationException;
import com.example.library_app_back_end.mapper.BookMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.library_app_back_end.constant.ErrorConstant.NOT_FOUND_OBJECT_LABEL;

@Log4j2
@Service
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CheckoutRepository checkoutRepository;
    private final HistoryRepository historyRepository;
    private final PaymentRepository paymentRepository;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper, CheckoutRepository checkoutRepository, HistoryRepository historyRepository, PaymentRepository paymentRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.checkoutRepository = checkoutRepository;
        this.historyRepository = historyRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<BookDTO> getAll(String title, String category) {

        log.debug("Request to get all books");
        List<Book> result = bookRepository.getBooks(title.toLowerCase(), category.toLowerCase());
        List<BookDTO> dtos = bookMapper.toDto(result);
        return dtos;
    }

    @Override
    public Page<BookDTO> getAllPaging(String title, String category, Pageable pageable) {
        log.debug("Request to get all books");
        Page<Book> books = bookRepository.getBooks(title.toLowerCase(), category.toLowerCase(), pageable);
        Page<BookDTO> result = books.map(bookMapper::toDto);
        return result;
    }

    @Override
    public BookDTO getBook(Long bookId) {
        log.debug("Request to get book by id");
        Book result = bookRepository.findById(bookId).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT,
                String.format(ErrorConstant.NOT_FOUND_OBJECT_LABEL, ErrorConstant.BOOK)));
        BookDTO dto = bookMapper.toDto(result);
        return dto;
    }

    @Override
    public Boolean checkOutBookByUser(String userEmail, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT,
                String.format(NOT_FOUND_OBJECT_LABEL, ErrorConstant.BOOK)));
        Optional<Checkout> validateCheckoutOpt = checkoutRepository.findFirstByUserEmailAndBookId(userEmail, bookId);
        if (!validateCheckoutOpt.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public BookDTO checkoutBook(String userEmail, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT,
                String.format(NOT_FOUND_OBJECT_LABEL, ErrorConstant.BOOK)));
        Optional<Checkout> validateCheckoutOpt = checkoutRepository.findFirstByUserEmailAndBookId(userEmail, bookId);
        //       check if book is already checked out or if book is not available
        if (!validateCheckoutOpt.isEmpty() || book.getCopiesAvailable() <= 0) {
            throw new ValidationException(ErrorConstant.INVALID_CHECKOUT, ErrorConstant.INVALID_CHECKOUT_LABEL);
        }
//   ADDITIONAL: NOT ALLOW USER TO CHECK OUT WHEN THERE IS LATE BOOK
//   ADDITIONAL:     find all books checked out and check if book needs return when the return date is later than now
        List<Checkout> currentBooksCheckedOut = checkoutRepository.findAllByUserEmail(userEmail);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean bookNeedsReturned = false;
        for (Checkout checkout : currentBooksCheckedOut) {
            try {
                Date d1 = sdf.parse(checkout.getReturnDate());
                Date d2 = sdf.parse(LocalDate.now().toString());
                TimeUnit time = TimeUnit.DAYS;
                double differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
                if (differenceInTime < 0) {
                    bookNeedsReturned = true;
                    break;
                }
            } catch (ParseException e) {
                throw new ValidationException(ErrorConstant.PARSE_ERROR, ErrorConstant.PARSE_ERROR_LABEL);
            }
        }
        //ADDITIONAL: then if current payment contain book that needs return then there is outstanding fees
        Optional<Payment> userPaymentOpt = paymentRepository.findFirstByUserEmail(userEmail);
        if (userPaymentOpt.isPresent()) {
            Payment payment = userPaymentOpt.get();
            if (payment.getAmount() > 0 && bookNeedsReturned) {
                throw new ValidationException(ErrorConstant.OUTSTANDING_FEES, ErrorConstant.OUTSTANDING_FEES_LABEL);
            }
//           if payment is exist add book price to payment
            payment.setAmount(payment.getAmount()+book.getPrice());
            paymentRepository.save(payment);
        }
        //ADDITIONAL: add new payment when book is checkout by user
        if (userPaymentOpt.isEmpty()) {
            Payment payment = new Payment();
            payment.setAmount(book.getPrice());
            payment.setUserEmail(userEmail);
            paymentRepository.save(payment);
        }

        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        bookRepository.save(book);
        //save check out
        Checkout checkout = Checkout.builder()
                .userEmail(userEmail)
                .checkoutDate(LocalDate.now().toString())
                .returnDate(LocalDate.now().plusDays(7).toString())
                .book(book)
                .build();
        checkoutRepository.save(checkout);

        BookDTO bookDTO = bookMapper.toDto(book);
        return bookDTO;
    }

    @Override
    public int currentCheckedOutCount(String userEmail) {
        return checkoutRepository.findAllByUserEmail(userEmail).size();
    }

    @Override
    public List<BookDTO> currentCheckedOutBooks(String userEmail) {
        List<BookDTO> bookDTOList = new ArrayList<>();

        List<Checkout> checkoutList = checkoutRepository.findAllByUserEmail(userEmail);
        List<Book> bookList = checkoutList.stream().map(Checkout::getBook).collect(Collectors.toList());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        bookList.forEach(book -> {
            Optional<Checkout> checkoutOpt = checkoutList.stream()
                    .filter(x -> x.getBook().getId() == book.getId()).findFirst();
            if (checkoutOpt.isPresent()) {
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = sdf.parse(checkoutOpt.get().getReturnDate());
                    d2 = sdf.parse(LocalDate.now().toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                TimeUnit time = TimeUnit.DAYS;
//                calculate days left to return date
                long differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
                BookDTO bookDTO = bookMapper.toDto(book);
                bookDTO.setDaysLeft((int) differenceInTime);
                bookDTOList.add(bookDTO);
            }
        });
        return bookDTOList;
    }

    @Override
    public BookDTO returnBook(String userEmail, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT,
                String.format(NOT_FOUND_OBJECT_LABEL, ErrorConstant.BOOK)));
//        check if book is checked out
        Checkout validateCheckout = checkoutRepository.findFirstByUserEmailAndBookId(userEmail, bookId).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT,
                String.format(NOT_FOUND_OBJECT_LABEL, ErrorConstant.BOOK_NOT_EXIST_CHECKOUT)));

        checkoutRepository.delete(validateCheckout);

        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        Book result = bookRepository.save(book);


//        ADDITIONAL: when user return late book, they will be add additional amount owes to the payment
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d1;
        Date d2;
        try {
            d1 = sdf.parse(validateCheckout.getReturnDate());
            d2 = sdf.parse(LocalDate.now().toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        TimeUnit time = TimeUnit.DAYS;
        double differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
        Payment payment = paymentRepository.findFirstByUserEmail(userEmail).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT, String.format(ErrorConstant.NOT_FOUND_OBJECT_LABEL,"Payment")));
        if (differenceInTime < 0) {
               payment.setAmount(payment.getAmount() + (differenceInTime*-1) - book.getPrice());
            paymentRepository.save(payment);
        }else {
            payment.setAmount(payment.getAmount() - book.getPrice());
            paymentRepository.save(payment);
        }

//        save returned book to history
        History history = History.builder()
                .userEmail(userEmail)
                .checkoutDate(validateCheckout.getCheckoutDate())
                .returnedDate(LocalDate.now().toString())
                .book(result)
                .build();
        historyRepository.save(history);

        return bookMapper.toDto(result);
    }

    @Override
    public BookDTO addBook(BookDTO bookRequest) {
        if (Objects.isNull(bookRequest.getTitle())) {
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "title"));
        }
        if (Objects.isNull(bookRequest.getAuthor())) {
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "author"));
        }
        if (Objects.isNull(bookRequest.getDescription())) {
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "description"));
        }
        if (Objects.isNull(bookRequest.getCopies())) {
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "copies"));
        }
        if (Objects.isNull(bookRequest.getCategory())) {
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "category"));
        }
        if (Objects.isNull(bookRequest.getImg())) {
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "image"));
        }
        if (Objects.isNull(bookRequest.getPrice())) {
            throw new ValidationException(ErrorConstant.BLANK_FIELD, String.format(ErrorConstant.BLANK_FIELD_LABEL, "price"));
        }
        Book book = Book.builder()
                .title(bookRequest.getTitle())
                .author(bookRequest.getAuthor())
                .description(bookRequest.getDescription())
                .copies(bookRequest.getCopies())
                .copiesAvailable(bookRequest.getCopies())
                .category(bookRequest.getCategory())
                .img(bookRequest.getImg())
                .price(bookRequest.getPrice())
                .build();
        Book result = bookRepository.save(book);
        return bookMapper.toDto(result);
    }

    @Override
    public BookDTO increaseBookQuantity(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT, String.format(NOT_FOUND_OBJECT_LABEL, "book")));
        book.setCopies(book.getCopies() + 1);
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        Book result = bookRepository.save(book);
        return bookMapper.toDto(result);
    }

    @Override
    public BookDTO decreaseBookQuantity(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT, String.format(NOT_FOUND_OBJECT_LABEL, "book")));

        if (book.getCopiesAvailable() <= 0 || book.getCopies() <= 0) {
            throw new ValidationException(ErrorConstant.BOOK, ErrorConstant.BOOK_QUANTITY_LOCKED);
        }
        book.setCopies(book.getCopies() - 1);
        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        Book result = bookRepository.save(book);
        return bookMapper.toDto(result);
    }

    @Override
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(()
                -> new ValidationException(ErrorConstant.NOT_FOUND_OBJECT, String.format(ErrorConstant.NOT_FOUND_OBJECT_LABEL, "book")));
        bookRepository.delete(book);
    }

}
