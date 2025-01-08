package ac.il.bgu.qa;
import ac.il.bgu.qa.errors.*;
import ac.il.bgu.qa.services.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.ArrayList;
import java.util.List;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestLibrary {
// Implement here


    private Library library;

    // creates a valid ID and user name for future using
    private final String ValidID = "617865027123";
    private final String ValidUserName = "Yali";
    private final String ValidISBN = "3790792363427";
    private final String ValidTitle = "How To Win Friends And Influence People";
    private final String ValidAuthor = "Dale Carnegie";

    @Mock
    private NotificationService notificationService;
    @Mock
    private DatabaseService databaseService;
    @Mock
    private ReviewService reviewService;
    @Mock
    private Book book;
    @Mock
    private User user;

    @Spy
    private List<String> reviews = spy(new ArrayList<>());

    /**
     * Initializes mocks and sets up the test environment before each test.
     * Clears the `reviews` list and creates a new instance of the `Library` class
     * using the mocked `DatabaseService` and `ReviewService`.
     */
    @BeforeEach
    public void setup_BeforeEach() {
        MockitoAnnotations.openMocks(this);
        reviews.clear();
        // Create a Library instance with the mock DatabaseService
        library = new Library(databaseService, reviewService);
    }

// *********************************************************Start of addBook Tests***************************************************************

    /**
     * Verifies that adding a null book to the library throws an IllegalArgumentException
     * with the appropriate message.
     */
    @Test
    void givenNullBook_WhenAddBook_ThenThrowIllegalArgumentException() {
        assertEquals("Invalid book.",
                assertThrows(IllegalArgumentException.class, () -> library.addBook(null)).getMessage());
    }

    /**
     * Verifies that adding a book with a null ISBN throws an IllegalArgumentException
     * with the appropriate message.
     */
    @Test
    void givenBookWithNullISBN_WhenAddBook_ThenThrowIllegalArgumentException() {
        when(book.getISBN()).thenReturn(null);
        assertEquals("Invalid ISBN.",
                assertThrows(IllegalArgumentException.class, () -> library.addBook(book)).getMessage());
    }

    /**
     * Verifies that adding a book with an invalid ISBN throws an IllegalArgumentException
     * with the appropriate message. Tests multiple invalid ISBN formats.
     *
     * @param invalidISBN an invalid ISBN string
     */
    @ParameterizedTest
    @ValueSource(strings = { "9--78-31-6-1-4-8-4-1-0-5", "1", "9780590353426", "???", "blabla",
            "9783161484105", "", "*@#$%^&*()!s2" })
    void givenBookWithInvalidISBN_WhenAddBook_ThenThrowIllegalArgumentException(String invalidISBN) {
        when(book.getISBN()).thenReturn(invalidISBN);
        assertEquals("Invalid ISBN.",
                assertThrows(IllegalArgumentException.class, () -> library.addBook(book)).getMessage());
    }

    /**
     * Verifies that adding a book with a null title throws an IllegalArgumentException
     * with the appropriate message.
     */
    @Test
    void givenBookWithNullTitle_WhenAddBook_ThenThrowIllegalArgumentException() {
        when(book.getISBN()).thenReturn(ValidISBN);
        when(book.getTitle()).thenReturn(null);
        assertEquals("Invalid title.",
                assertThrows(IllegalArgumentException.class, () -> library.addBook(book)).getMessage());
    }

    /**
     * Verifies that adding a book with an empty title throws an IllegalArgumentException
     * with the appropriate message.
     */
    @Test
    void givenBookWithEmptyTitle_WhenAddBook_ThenThrowIllegalArgumentException() {
        when(book.getISBN()).thenReturn(ValidISBN);
        when(book.getTitle()).thenReturn("");
        assertEquals("Invalid title.",
                assertThrows(IllegalArgumentException.class, () -> library.addBook(book)).getMessage());
    }

    /**
     * Verifies that adding a book with a null author throws an IllegalArgumentException
     * with the appropriate message.
     */
    @Test
    void givenBookWithNullAuthor_WhenAddBook_ThenThrowIllegalArgumentException() {
        when(book.getISBN()).thenReturn(ValidISBN);
        when(book.getTitle()).thenReturn(ValidTitle);
        when(book.getAuthor()).thenReturn(null);
        assertEquals("Invalid author.",
                assertThrows(IllegalArgumentException.class, () -> library.addBook(book)).getMessage());
    }

    /**
     * Verifies that adding a book with an invalid author name throws an IllegalArgumentException
     * with the appropriate message. Tests multiple invalid author name formats.
     *
     * @param invalidAuthorName an invalid author name string
     */
    @ParameterizedTest
    @ValueSource(strings = { "", "32131How To Win Friends And Influence People", "Blabla32131",
            "Dale $$$Carnegie", "D **&^%$ C", "Dale--Carnegie", "Dale'''Carnegie" })
    void givenBookWithInvalidAuthor_WhenAddBook_ThenThrowIllegalArgumentException(String invalidAuthorName) {
        when(book.getISBN()).thenReturn(ValidISBN);
        when(book.getTitle()).thenReturn(ValidTitle);
        when(book.getAuthor()).thenReturn(invalidAuthorName);
        assertEquals("Invalid author.",
                assertThrows(IllegalArgumentException.class, () -> library.addBook(book)).getMessage());
    }

    /**
     * Verifies that adding a book that is already borrowed throws an IllegalArgumentException
     * with the appropriate message.
     */
    @Test
    void givenAlreadyBorrowedBook_WhenAddBook_ThenThrowIllegalArgumentException() {
        when(book.getISBN()).thenReturn(ValidISBN);
        when(book.getTitle()).thenReturn(ValidTitle);
        when(book.getAuthor()).thenReturn(ValidAuthor);
        when(book.isBorrowed()).thenReturn(true);
        assertEquals("Book with invalid borrowed state.",
                assertThrows(IllegalArgumentException.class, () -> library.addBook(book)).getMessage());
    }

    /**
     * Verifies that adding a book that already exists in the database throws an IllegalArgumentException
     * with the appropriate message. Also verifies that the `getBookByISBN` method is called once.
     */
    @Test
    void givenBookAlreadyInDB_WhenAddBook_ThenThrowIllegalArgumentException() {
        when(book.getISBN()).thenReturn(ValidISBN);
        when(book.getTitle()).thenReturn(ValidTitle);
        when(book.getAuthor()).thenReturn(ValidAuthor);
        when(databaseService.getBookByISBN(book.getISBN())).thenReturn(book);
        assertEquals("Book already exists.",
                assertThrows(IllegalArgumentException.class, () -> library.addBook(book)).getMessage());
        verify(databaseService, times(1)).getBookByISBN(book.getISBN());
    }

    /**
     * Verifies that adding a valid book succeeds. Ensures the book is added to the library
     * and the appropriate database methods are called with the correct arguments.
     */
    @Test
    void givenValidBook_WhenAddBook_ThenBookAddedToLibrary() {
        when(book.getISBN()).thenReturn(ValidISBN);
        when(book.getTitle()).thenReturn(ValidTitle);
        when(book.getAuthor()).thenReturn(ValidAuthor);
        when(book.isBorrowed()).thenReturn(false);
        when(databaseService.getBookByISBN(book.getISBN())).thenReturn(null);
        library.addBook(book);
        verify(databaseService, times(1)).getBookByISBN(book.getISBN());
        verify(databaseService, times(1)).addBook(book.getISBN(), book);
    }
// *********************************************************End of addBook Tests***************************************************************

// *********************************************************Start of registerUser Tests***************************************************************
    /**
     * Tests that registering a null user throws an IllegalArgumentException
     * with the message "Invalid user."
     */
    @Test
    void givenNullUser_WhenRegisterUser_ThenThrowIllegalArgumentException() {
        assertEquals("Invalid user.",
                assertThrows(IllegalArgumentException.class, () -> library.registerUser(null)).getMessage());
    }

    /**
     * Tests that registering a user with a null ID throws an IllegalArgumentException
     * with the message "Invalid user Id."
     */
    @Test
    void givenUserWithNullID_WhenRegisterUser_ThenThrowIllegalArgumentException() {
        // Stubbing
        when(user.getId()).thenReturn(null);
        // Action
        assertEquals("Invalid user Id.",
                assertThrows(IllegalArgumentException.class, () -> library.registerUser(user)).getMessage());
    }

    /**
     * Tests that registering a user with an invalid ID throws an IllegalArgumentException
     * with the message "Invalid user Id." This test uses multiple invalid ID formats.
     *
     * @param invalidID an invalid ID string
     */
    @ParameterizedTest
    @ValueSource(strings = { "aaaaaaaaaaaa", "2333333!???114", "239!@#$%^&*()42", "647,383,0841", "11111?123213",
            "123331323;132213", "1111", "111", "111222333444555666", "123", " ", "" })
    void givenUserWithInvalidID_WhenRegisterUser_ThenThrowIllegalArgumentException(String invalidID) {
        when(user.getId()).thenReturn(invalidID);
        assertEquals("Invalid user Id.",
                assertThrows(IllegalArgumentException.class, () -> library.registerUser(user)).getMessage());
    }

    /**
     * Tests that registering a user with a null name throws an IllegalArgumentException
     * with the message "Invalid user name."
     */
    @Test
    void givenUserWithNullName_WhenRegisterUser_ThenThrowIllegalArgumentException() {
        // Stubbing
        when(user.getId()).thenReturn(ValidID);
        when(user.getName()).thenReturn(null);
        // Action
        assertEquals("Invalid user name.",
                assertThrows(IllegalArgumentException.class, () -> library.registerUser(user)).getMessage());
    }

    /**
     * Tests that registering a user with an empty name throws an IllegalArgumentException
     * with the message "Invalid user name."
     */
    @Test
    void givenUserWithEmptyName_WhenRegisterUser_ThenThrowIllegalArgumentException() {
        // Stubbing
        when(user.getId()).thenReturn(ValidID);
        when(user.getName()).thenReturn("");
        // Action
        assertEquals("Invalid user name.",
                assertThrows(IllegalArgumentException.class, () -> library.registerUser(user)).getMessage());
    }

    /**
     * Tests that registering a user with a null notification service throws an IllegalArgumentException
     * with the message "Invalid notification service."
     */
    @Test
    void givenUserWithNullNotificationService_WhenRegisterUser_ThenThrowIllegalArgumentException() {
        // Stubbing
        when(user.getId()).thenReturn(ValidID);
        when(user.getName()).thenReturn(ValidUserName);
        when(user.getNotificationService()).thenReturn(null);
        // Action
        assertEquals("Invalid notification service.",
                assertThrows(IllegalArgumentException.class, () -> library.registerUser(user)).getMessage());
    }

    /**
     * Tests that registering a user who is already in the database throws an IllegalArgumentException
     * with the message "User already exists." Ensures the `getUserById` method is called once.
     */
    @Test
    void givenUserAlreadyInDB_WhenRegisterUser_ThenThrowIllegalArgumentException() {
        // Stubbing
        when(user.getName()).thenReturn(ValidUserName);
        when(user.getId()).thenReturn(ValidID);
        when(user.getNotificationService()).thenReturn(notificationService);
        when(databaseService.getUserById(anyString())).thenReturn(user);
        // Action
        assertEquals("User already exists.",
                assertThrows(IllegalArgumentException.class, () -> library.registerUser(user)).getMessage());
        verify(databaseService, times(1)).getUserById(anyString());
    }

    /**
     * Tests that registering a valid user succeeds without throwing an exception.
     * Verifies that the `registerUser` method in the database service is called once
     * with the correct arguments.
     */
    @Test
    void givenValidUser_WhenRegisterUser_ThenUserRegistered() {
        User validUser = new User(ValidUserName, ValidID, notificationService);
        // Ensures no exception is thrown for a valid user
        assertDoesNotThrow(() -> {
            // Action
            library.registerUser(validUser);
        });
        // Verify interactions
        verify(databaseService, times(1)).registerUser(validUser.getId(), validUser);
    }

// *********************************************************End of registerUser Tests***************************************************************


// *********************************************************Start of borrowBook Tests***************************************************************
    /**
     * Tests that borrowing a book with a null ISBN throws an IllegalArgumentException
     * with the message "Invalid ISBN."
     */
    @Test
    void givenNullISBN_WhenBorrowBook_ThenThrowIllegalArgumentException() {
        assertEquals("Invalid ISBN.",
            assertThrows(IllegalArgumentException.class, () -> library.borrowBook(null, "0")).getMessage());
    }

    /**
     * Tests that borrowing a book with an invalid ISBN throws an IllegalArgumentException
     * with the message "Invalid ISBN." This test uses multiple invalid ISBN formats.
     *
     * @param invalidISBN an invalid ISBN string
     */
    @ParameterizedTest
    @ValueSource(strings = { "9--78-31-6-1-4-8-4-1-0-5", "1", "9780590353426", "???", "blabla",
                             "9783161484105", "", "*@#$%^&*()!s2" })
    void givenInvalidISBN_WhenBorrowBook_ThenThrowIllegalArgumentException(String invalidISBN) {
        assertEquals("Invalid ISBN.",
            assertThrows(IllegalArgumentException.class, () -> library.borrowBook(invalidISBN, "0")).getMessage());
    }

    /**
     * Tests that attempting to borrow a book that is not in the database throws a BookNotFoundException
     * with the message "Book not found!" Ensures that the `getBookByISBN` method is called once.
     */
    @Test
    void givenBookNotInDB_WhenBorrowBook_ThenThrowBookNotFoundException() {
        when(databaseService.getBookByISBN(ValidISBN)).thenReturn(null);
        assertEquals("Book not found!",
            assertThrows(BookNotFoundException.class, () -> library.borrowBook(ValidISBN, "0")).getMessage());
        verify(databaseService, times(1)).getBookByISBN(ValidISBN);
    }

    /**
     * Tests that borrowing a book with an invalid user ID throws an IllegalArgumentException
     * with the message "Invalid user Id." This test uses multiple invalid user ID formats.
     *
     * @param invalidUserID an invalid user ID string
     */
    @ParameterizedTest
    @ValueSource(strings = { "aaaaaaaaaaaa", "2333333!???114", "647,383,084", "11111?123213",
                             "123331323;132213", "1111", "111", "111222333444555666", "123", " ", "" })
    void givenInvalidUserID_WhenBorrowBook_ThenThrowIllegalArgumentException(String invalidUserID) {
        when(databaseService.getBookByISBN(ValidISBN)).thenReturn(book);
        assertEquals("Invalid user Id.",
            assertThrows(IllegalArgumentException.class, () -> library.borrowBook(ValidISBN, invalidUserID)).getMessage());
    }

    /**
     * Tests that borrowing a book with a null user ID throws an IllegalArgumentException
     * with the message "Invalid user Id."
     */
    @Test
    void givenNullUserID_WhenBorrowBook_ThenThrowIllegalArgumentException() {
        when(databaseService.getBookByISBN(ValidISBN)).thenReturn(book);
        assertEquals("Invalid user Id.",
            assertThrows(IllegalArgumentException.class, () -> library.borrowBook(ValidISBN, null)).getMessage());
    }

    /**
     * Tests that borrowing a book with a user who is not in the database throws a UserNotRegisteredException
     * with the message "User not found!" Ensures that the `getUserById` method is called once.
     */
    @Test
    public void givenUserNotInDB_WhenBorrowBook_ThenThrowUserNotRegisteredException() {
        when(databaseService.getBookByISBN(ValidISBN)).thenReturn(book);
        when(databaseService.getUserById(ValidID)).thenReturn(null);
        assertEquals("User not found!",
            assertThrows(UserNotRegisteredException.class, () -> library.borrowBook(ValidISBN, ValidID)).getMessage());
        verify(databaseService, times(1)).getUserById(ValidID);
    }

    /**
     * Tests that borrowing a book that is already borrowed throws a BookAlreadyBorrowedException
     * with the message "Book is already borrowed!"
     */
    @Test
    void givenAlreadyBorrowedBook_WhenBorrowBook_ThenThrowBookAlreadyBorrowedException() {
        when(databaseService.getUserById(ValidID)).thenReturn(user);
        when(databaseService.getBookByISBN(ValidISBN)).thenReturn(book);
        when(book.isBorrowed()).thenReturn(true);
        assertEquals("Book is already borrowed!",
            assertThrows(BookAlreadyBorrowedException.class, () -> library.borrowBook(ValidISBN, ValidID)).getMessage());
    }

    /**
     * Tests that borrowing a valid book by a valid user succeeds. Ensures that the
     * book's `borrow` method and the database's `borrowBook` method are called once
     * with the correct arguments.
     */
    @Test
    void givenValidBookAndUser_WhenBorrowBook_ThenBookBorrowed() {
        when(databaseService.getUserById(ValidID)).thenReturn(user);
        when(databaseService.getBookByISBN(ValidISBN)).thenReturn(book);
        when(book.isBorrowed()).thenReturn(false);
        library.borrowBook(ValidISBN, ValidID);
        verify(book, times(1)).borrow();
        verify(databaseService, times(1)).borrowBook(ValidISBN, ValidID);
    }
// *********************************************************End of borrowBook Tests***************************************************************


// *********************************************************Start of returnBook Tests***************************************************************

    @Test
    void givenNullISBN_WhenReturnBook_ThenThrowIllegalArgumentException() {
        // make sure adding this invalid book throw IllegalArgumentException
        assertEquals("Invalid ISBN.",assertThrows(IllegalArgumentException.class, () -> library.returnBook(null)).getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = { "9--78-31-6-1-4-8-4-1-0-5", "1", "9780590353426", "???", "blabla",
            "9783161484105", "", "*@#$%^&*()!s2" })
    void givenInvalidISBN_WhenReturnBook_ThenThrowIllegalArgumentException(String invalidISBN) {
        // Action & Assertion
        assertEquals("Invalid ISBN.", assertThrows(IllegalArgumentException.class, () -> library.borrowBook(invalidISBN,ValidID)).getMessage());
    }

    @Test
    void givenBookNotInDB_WhenReturnBook_ThenThrowBookNotFoundException() {
        //stub to say the book not in db
        when(databaseService.getBookByISBN(ValidISBN)).thenReturn(null);
        assertEquals("Book not found!", assertThrows(BookNotFoundException.class, () -> library.returnBook(ValidISBN)).getMessage());
        //checking databaseService.addBook was called with the correct arguments
        verify(databaseService, times(1)).getBookByISBN(ValidISBN);
    }

    @Test
    void givenBookNotBorrowed_WhenReturnBook_ThenThrowBookNotBorrowedException() {
        // Stubbing
        when(databaseService.getBookByISBN(ValidISBN)).thenReturn(book);
        when(book.isBorrowed()).thenReturn(false);
        // make sure adding a borrowed book throw IllegalArgumentException
        assertEquals("Book wasn't borrowed!",assertThrows(BookNotBorrowedException.class, () -> library.returnBook(ValidISBN)).getMessage());
    }

    @Test
    void givenValidBook_WhenReturnBook_ThenBookReturned() {
        // Stubbing
        when(databaseService.getBookByISBN(ValidISBN)).thenReturn(book);
        when(book.isBorrowed()).thenReturn(true);
        // Action
        library.returnBook(ValidISBN);
        // Verification
        verify(book, times(1)).returnBook();
        verify(databaseService, times(1)).returnBook(ValidISBN);
    }

// *********************************************************End of returnBook Tests***************************************************************

// *********************************************************Start of notifyUserWithBookReviews Tests***************************************************************
    /**
     * Tests that attempting to notify a user with reviews of a book with an invalid ISBN
     * throws an IllegalArgumentException with the message "Invalid ISBN."
     *
     * @param ISBN an invalid ISBN string
     */
    @ParameterizedTest
    @ValueSource(strings = { "9--78-31-6-1-4-8-4-1-0-5", "1", "9780590353426", "???", "blabla",
            "9783161484105", "", "*@#$%^&*()!s2" })
    void givenInvalidISBN_WhenNotifyUserWithBookReviews_ThenThrowIllegalArgumentException(String ISBN) {
        assertEquals("Invalid ISBN.",
                assertThrows(IllegalArgumentException.class, () -> library.notifyUserWithBookReviews(ISBN, ValidID)).getMessage());
    }

    /**
     * Tests that attempting to notify a user with reviews of a book with a null ISBN
     * throws an IllegalArgumentException with the message "Invalid ISBN."
     */
    @Test
    void givenNullISBN_WhenNotifyUserWithBookReviews_ThenThrowIllegalArgumentException() {
        assertEquals("Invalid ISBN.",
                assertThrows(IllegalArgumentException.class, () -> library.notifyUserWithBookReviews(null, ValidID)).getMessage());
    }

    /**
     * Tests that attempting to notify a user with reviews of a book with a null user ID
     * throws an IllegalArgumentException with the message "Invalid user Id."
     */
    @Test
    void givenNullUserID_WhenNotifyUserWithBookReviews_ThenThrowIllegalArgumentException() {
        assertEquals("Invalid user Id.",
                assertThrows(IllegalArgumentException.class, () -> library.notifyUserWithBookReviews(ValidISBN, null)).getMessage());
    }

    /**
     * Tests that attempting to notify a user with an invalid user ID throws an IllegalArgumentException
     * with the message "Invalid user Id." This test uses multiple invalid user ID formats.
     *
     * @param userID an invalid user ID string
     */
    @ParameterizedTest
    @ValueSource(strings = { "aaaaaaaaaaaa", "2333333!???114", "647,383,084", "11111?123213",
            "123331323;132213", "1111", "111", "111222333444555666", "123", " ", "" })
    void givenUserWithInvalidID_WhenNotifyUserWithBookReviews_ThenThrowIllegalArgumentException(String userID) {
        assertEquals("Invalid user Id.",
                assertThrows(IllegalArgumentException.class, () -> library.notifyUserWithBookReviews(ValidISBN, userID)).getMessage());
    }

    /**
     * Tests that attempting to notify a user with reviews for a book that is not in the database
     * throws a BookNotFoundException with the message "Book not found!"
     * Ensures the `getBookByISBN` method is called once.
     */
    @Test
    void givenNullBook_WhenNotifyUserWithBookReviews_ThenThrowBookNotFoundException() {
        when(databaseService.getBookByISBN(anyString())).thenReturn(null);
        assertEquals("Book not found!",
                assertThrows(BookNotFoundException.class, () -> library.notifyUserWithBookReviews(ValidISBN, ValidID)).getMessage());
        verify(databaseService, times(1)).getBookByISBN(ValidISBN);
    }

    /**
     * Tests that attempting to notify a user who is not in the database with reviews of a book
     * throws a UserNotRegisteredException with the message "User not found!"
     */
    @Test
    void givenNullUser_WhenNotifyUserWithBookReviews_ThenThrowUserNotRegisteredException() {
        when(databaseService.getBookByISBN(anyString())).thenReturn(book);
        when(databaseService.getUserById(anyString())).thenReturn(null);
        assertEquals("User not found!",
                assertThrows(UserNotRegisteredException.class, () -> library.notifyUserWithBookReviews(ValidISBN, ValidID)).getMessage());
    }

    /**
     * Tests that attempting to notify a user with reviews of a book that has no reviews
     * throws a NoReviewsFoundException with the message "No reviews found!"
     */
    @Test
    void givenNullReviews_WhenNotifyUserWithBookReviews_ThenThrowNoReviewsFoundException() {
        when(reviewService.getReviewsForBook(anyString())).thenReturn(null);
        when(databaseService.getBookByISBN(anyString())).thenReturn(book);
        when(databaseService.getUserById(anyString())).thenReturn(user);
        assertEquals("No reviews found!",
                assertThrows(NoReviewsFoundException.class, () -> library.notifyUserWithBookReviews(ValidISBN, ValidID)).getMessage());
    }

    /**
     * Tests that attempting to notify a user with reviews of a book that has an empty list of reviews
     * throws a NoReviewsFoundException with the message "No reviews found!"
     */
    @Test
    void givenEmptyReviews_WhenNotifyUserWithBookReviews_ThenThrowNoReviewsFoundException() {
        when(databaseService.getBookByISBN(anyString())).thenReturn(book);
        when(databaseService.getUserById(anyString())).thenReturn(user);
        when(reviewService.getReviewsForBook(anyString())).thenReturn(reviews);
        assertEquals("No reviews found!",
                assertThrows(NoReviewsFoundException.class, () -> library.notifyUserWithBookReviews(ValidISBN, ValidID)).getMessage());
    }

    /**
     * Tests that attempting to notify a user with reviews of a book when the review service
     * throws an exception results in a ReviewServiceUnavailableException with the message
     * "Review service unavailable!"
     */
    @Test
    void givenReviewsException_WhenNotifyUserWithBookReviews_ThenThrowReviewServiceUnavailableException() {
        when(databaseService.getBookByISBN(anyString())).thenReturn(book);
        when(databaseService.getUserById(anyString())).thenReturn(user);
        ReviewException exm = new ReviewException("Review exception");
        doThrow(exm).when(reviewService).getReviewsForBook(anyString());
        assertEquals("Review service unavailable!",
                assertThrows(ReviewServiceUnavailableException.class, () -> library.notifyUserWithBookReviews(ValidISBN, ValidID)).getMessage());
    }

    /**
     * Tests that attempting to notify a user with reviews of a book when the notification service
     * throws an exception results in a NotificationException with the message "Notification failed!"
     */
    @Test
    void givenInvalidNotification_WhenNotifyUserWithBookReviews_ThenThrowNotificationException() {
        when(databaseService.getBookByISBN(anyString())).thenReturn(book);
        when(databaseService.getUserById(anyString())).thenReturn(user);

        reviews.add("Bad review");
        reviews.add("Good review");
        when(reviewService.getReviewsForBook(anyString())).thenReturn(reviews);
        NotificationException exm = new NotificationException("Notification exception");
        doThrow(exm).when(user).sendNotification(anyString());
        assertEquals("Notification failed!",
                assertThrows(NotificationException.class, () -> library.notifyUserWithBookReviews(ValidISBN, ValidID)).getMessage());
    }

    /**
     * Tests that a valid notification is sent to a user for a book with reviews.
     * Ensures that the notification is sent, reviews are fetched, and the review service
     * is properly closed.
     */
    @Test
    void givenValidBookReviewsAndUser_WhenNotifyUserWithBookReviews_ThenNotificationSent() {
        when(databaseService.getBookByISBN(anyString())).thenReturn(book);
        when(databaseService.getUserById(anyString())).thenReturn(user);

        reviews.add("Good review");
        when(reviewService.getReviewsForBook(anyString())).thenReturn(reviews);
        assertDoesNotThrow(() -> library.notifyUserWithBookReviews(ValidISBN, ValidID));
        verify(user, times(1)).sendNotification("Reviews for '" + book.getTitle() + "':\n" + String.join("\n", reviews));
        verify(reviewService, times(1)).getReviewsForBook(ValidISBN);
        verify(reviewService, times(1)).close();
    }

// *********************************************************End of notifyUserWithBookReviews Tests***************************************************************

// *********************************************************Start of getBookByISBN Tests***************************************************************
    @ParameterizedTest
    @ValueSource(strings = { "9--78-31-6-1-4-8-4-1-0-5", "1", "9780590353426", "???", "blabla",
            "9783161484105", "", "*@#$%^&*()!s2" })
    void givenInvalidISBN_WhenGetBookByISBN_ThenThrowIllegalArgumentException(String ISBN) {
        // Action & Assertion
        assertEquals("Invalid ISBN.",assertThrows(IllegalArgumentException.class, () -> library.getBookByISBN(ISBN, ValidID)).getMessage());
    }
    @Test
    void givenNullISBN_WhenGetBookByISBN_ThenThrowIllegalArgumentException() {
        // Action & Assertion
        assertEquals("Invalid ISBN.",assertThrows(IllegalArgumentException.class, () -> library.getBookByISBN(null, ValidID)).getMessage());
    }

    @Test
    void givenNullUserID_WhenGetBookByISBN_ThenThrowIllegalArgumentException() {
        // Action
        assertEquals("Invalid user Id.",assertThrows(IllegalArgumentException.class, () -> library.getBookByISBN(ValidISBN, null)).getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = { "aaaaaaaaaaaa","2333333!???114","647,383,084","11111?123213",
            "123331323;132213","1111","111","111222333444555666","123"," ", ""})
    void givenInvalidUserID_WhenGetBookByISBN_ThenThrowIllegalArgumentException(String userID) {
        assertEquals("Invalid user Id.", assertThrows(IllegalArgumentException.class, () -> library.getBookByISBN(ValidISBN, userID)).getMessage());
    }

    @Test
    void givenNullBook_WhenGetBookByISBN_ThenThrowBookNotFoundException() {
        // Stub to simulate book not found (null)
        when(databaseService.getBookByISBN(anyString())).thenReturn(null);
        // Action & Assertion
        assertEquals("Book not found!", assertThrows(BookNotFoundException.class, () -> library.getBookByISBN(ValidISBN, ValidID)).getMessage());
    }

    @Test
    void givenAlreadyBorrowedBook_WhenGetBookByISBN_ThenThrowBookAlreadyBorrowedException() {
        // Stub to simulate book
        when(databaseService.getBookByISBN(anyString())).thenReturn(book);
        // book is borrowed
        when(book.isBorrowed()).thenReturn(true);
        // Action & Assertion
        assertEquals("Book was already borrowed!", assertThrows(BookAlreadyBorrowedException.class, () -> library.getBookByISBN(ValidISBN, ValidID)).getMessage());
    }

    /**
     * Tests the scenario where the notification process fails (e.g., due to a null user)
     * during the retrieval of a book by its ISBN. Ensures that the exception does not
     * propagate and the book is returned regardless of the notification failure.
     */
    @Test
    void givenFailToNotify_WhenGetBookByISBN_ThenIgnoreAndReturnBook() {
        // Stub to simulate the book being fetched from the database service.
        // The `getBookByISBN` method is set to return a mocked `book` object for any ISBN input.
        when(databaseService.getBookByISBN(anyString())).thenReturn(book);

        // Simulate a book that is not borrowed.
        // The `isBorrowed` method of the mocked `book` object is set to return `false`,
        // indicating that the book is available to be borrowed.
        when(book.isBorrowed()).thenReturn(false);

        // Simulate a failure in the notification process by stubbing the `getUserById` method
        // of the database service to return `null`. This represents a scenario where the user
        // attempting to borrow the book does not exist in the system, causing the notification
        // process to fail. However, the system must still return the book despite the notification failure.
        when(databaseService.getUserById(anyString())).thenReturn(null);

        // Action: Invoke the `getBookByISBN` method and ensure that no exception is thrown.
        // This verifies that the failure in the notification process (triggered by the null user)
        // does not interrupt the retrieval of the book by its ISBN.
        assertDoesNotThrow(() -> library.getBookByISBN(ValidISBN, ValidID));
    }

    /**
     * Tests the scenario where a valid ISBN and user ID are provided for retrieving a book.
     * Verifies that the book is correctly returned and that the appropriate database service
     * calls are made, including during the notification process.
     */
    @Test
    void givenValidISBNAndUserID_WhenGetBookByISBN_ThenReturnBook() {
        // Stub the behavior of the `databaseService.getBookByISBN` method to simulate the database response.
        // Regardless of the ISBN provided, it returns a mocked `book` object. This setup ensures
        // controlled conditions for testing without relying on an actual database.
        when(databaseService.getBookByISBN(anyString())).thenReturn(book);

        // Simulate a book that is not currently borrowed.
        // The `isBorrowed` method of the mocked `book` object is stubbed to return `false`,
        // indicating that the book is available for the user to borrow or interact with.
        when(book.isBorrowed()).thenReturn(false);

        // Action: Call the `library.getBookByISBN` method with a valid ISBN and user ID.
        // This triggers the core functionality of retrieving a book based on the provided parameters.
        Book resultBook = library.getBookByISBN(ValidISBN, ValidID);

        // Assertion: Ensure that the returned result is not null.
        // This confirms that a valid book object is retrieved when the correct ISBN and user ID are provided.
        assertNotNull(resultBook);

        // Verification: Ensure the `getBookByISBN` method of the `databaseService` is called exactly twice.
        // - The first call occurs during the main process of fetching the book.
        // - The second call happens in the `notifyUserWithBookReviews` method, which is invoked
        //   as part of the `getBookByISBN` process. This verifies the integration between fetching the book
        //   and notifying the user about its reviews.
        verify(databaseService, times(2)).getBookByISBN(ValidISBN);
    }

// *********************************************************End of getBookByISBN Tests***************************************************************


}