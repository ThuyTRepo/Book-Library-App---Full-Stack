import BookModel from "./BookModel";

class HistoryModel {
    id: number;
    userEmail: string;
    checkoutDate: string;
    returnedDate: string;
    book: BookModel;

    constructor(id: number, userEmail: string, checkoutDate: string, returnedDate: string, 
        book:BookModel) {
            this.id = id;
            this.userEmail = userEmail;
            this.checkoutDate = checkoutDate;
            this.returnedDate = returnedDate;
            this.book = book;
        }
}

export default HistoryModel;