import { useEffect, useState } from "react"
import BookModel from "../../models/BookModel";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import { SearchBook } from "./components/SearchBook";
import { Pagination } from "../Utils/Pagination";
export const SearchBooksPage = () => {

    const [books, setBooks] = useState<BookModel[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [httpError, setHttpError] = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [booksPerPage] = useState(5);
    const [totalAmountOfBooks, setTotalAmountOfBooks] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    // search by title
    const [search, setSearch] = useState('');
    const [searchUrl, setSearchUrl] = useState('');
    // search by category
    const [categorySelection, setCategorySelection] = useState('Book category');

    useEffect(() => {
        const fetchBooks = async () => {
            const baseUrl = `${process.env.REACT_APP_API}/books`;
            const encodedSearch = encodeURIComponent(search);
            const encodedCategory = categorySelection !== 'Book category' && categorySelection !== 'All'
                                    ? encodeURIComponent(categorySelection) : '';
            const url = `${baseUrl}?page=${currentPage - 1}&pageSize=${booksPerPage}${encodedSearch ? `&title=${encodedSearch}` : ''}${encodedCategory ? `&category=${encodedCategory}` : ''}`;

            try {
                const response = await fetch(url);
                if (!response.ok) {
                    throw new Error('Something went wrong!');
                }
                const responseJson = await response.json();
                const responseData = responseJson.data.items;
                setTotalAmountOfBooks(responseJson.data.total);
                setTotalPages(Math.ceil(responseJson.data.total / booksPerPage));
               
                const loadedBooks: BookModel[] = [];

                for (const key in responseData) {
                    loadedBooks.push({
                        id: responseData[key].id,
                        title: responseData[key].title,
                        author: responseData[key].author,
                        description: responseData[key].description,
                        copies: responseData[key].copies,
                        copiesAvailable: responseData[key].copiesAvailable,
                        category: responseData[key].category,
                        img: responseData[key].img,
                    });
                }
                setBooks(loadedBooks);
                setIsLoading(false);
            } catch (error:any) {
                setHttpError(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        fetchBooks();
        window.scrollTo(0, 0);
    }, [currentPage, booksPerPage, search, categorySelection]);

    if (isLoading) {
        return (
            <SpinnerLoading />
        )
    }

    if (httpError) {
        return (
            <div className='container m-5'>
                <p>{httpError}</p>
            </div>
        )
    }

    // search by title and category
    const searchHandleChange = () => {
        let newUrl = `?page=<pageNumber>&size=${booksPerPage}`;
        if (search) {
            newUrl += `&title=${search}`;
        }
        if (categorySelection !== 'Book category' && categorySelection !== 'All') { // Assuming 'Book category' is the default or placeholder text
            newUrl += `&category=${categorySelection}`;
        }
        setSearchUrl(newUrl);
    }
    const handleCategorySelection = (category:string) => {
        setCategorySelection(category);
    }
    

    const indexOfLastBook: number = currentPage * booksPerPage;
    const indexOfFirstBook: number = indexOfLastBook - booksPerPage;
    let lastItem = booksPerPage * currentPage <= totalAmountOfBooks ? booksPerPage * currentPage : totalAmountOfBooks;
    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    return (
        <div>
            <div className='container'>
                <div>
                    <div className='row mt-5'>
                        <div className='col-6'>
                            <div className='d-flex'>
                                <input className='form-control me-2' type='search'
                                    placeholder='Search' aria-labelledby='Search'
                                    onChange={e => setSearch(e.target.value)} />
                                <button className='btn btn-outline-success'
                                    onClick={() => searchHandleChange()}>
                                    Search
                                </button>
                            </div>
                        </div>
                        <div className='col-4'>
                            <div className='dropdown'>
                                <button className='btn btn-secondary dropdown-toggle' type='button'
                                    id='dropdownMenuButton1' data-bs-toggle='dropdown'
                                    aria-expanded='false'>
                                    {categorySelection}
                                </button>
                                <ul className='dropdown-menu' aria-labelledby='dropdownMenuButton1'>
                                    <li onClick={()=> handleCategorySelection('All')}>
                                        <a className='dropdown-item' href='#'>
                                            All
                                        </a>
                                    </li>
                                    <li onClick={()=> handleCategorySelection('FE')}>
                                        <a className='dropdown-item' href='#'>
                                            Front End
                                        </a>
                                    </li>
                                    <li onClick={()=> handleCategorySelection('BE') }>
                                        <a className='dropdown-item' href='#'>
                                            Back End
                                        </a>
                                    </li>
                                    <li onClick={()=> handleCategorySelection('Data')}>
                                        <a className='dropdown-item' href='#'>
                                            Data
                                        </a>
                                    </li>
                                    <li onClick={()=> handleCategorySelection('Devops')}>
                                        <a className='dropdown-item' href='#'>
                                            DevOps
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    {totalAmountOfBooks > 0 ?
                        <>
                            <div className='mt-3'>
                                <h5>Number of results: ({totalAmountOfBooks})</h5>
                            </div>
                            <p>
                                {indexOfFirstBook + 1} to {lastItem} of {totalAmountOfBooks} items:
                            </p>
                            {books.map(book => (
                                <SearchBook book={book} key={book.id} />
                            ))}
                        </>
                        :
                        <div className='m-5'>
                            <h3>
                                Can't find what you are looking for?
                            </h3>
                            <a type='button' className='btn main-color btn-md px-4 me-md-2 fw-bold text-white'
                                href='#'>Library Services</a>
                        </div>
                    }

                    {totalPages > 1 &&
                        <Pagination currentPage={currentPage} totalPages={totalPages} paginate={paginate} />
                    }
                </div>
            </div>
        </div>
    );
}