/******************** 이벤트 **********************/
$('.btn-search').on('click', () => {
    fnSearch();
});

$(document).on('click', '.none-item', () => {
    location.href='/review/reviewWrite.page'
})

/******************** 함수 **********************/

// 검색 버튼
const fnSearch = () => {

    // input안의 내용 가져오기
    let searchKeyword = $('.main-searchform > input').val();

    // /product/searchProduct 경로로 보내기
    axios.get('/product/searchProduct', {
        params: {
            searchKeyword: searchKeyword
        }
    })
    .then((response) => {
        console.log("searchResult", response.data.productList);
        fnShowSearchResult(response.data.productList);
    })
    .catch((error) => {
        alert(error.response.data.message);
    })
}

// 검색 결과 표시
const fnShowSearchResult = (productList) => {

    /*
        {
        "productList": [
            {
                "productId": 1,
                "reviewCount": 2,
                "productName": "새우깡",
                "subCategory": "과자",
                "topCategory": "스낵/과자류",
                "ratingTasteAverage": 4.5,
                "ratingPriceAverage": 3.5
                }
            ]
        }
     */

    let resultSection = $('.result-section');
    resultSection.empty();

    // 결과가 없을 경우
    if(productList.length === 0) {
        let str = '<div class="col-md-6" data-aos="fade-up" data-aos-delay="500">';
        str += '<div class="service-item none-item d-flex position-relative h-100">';
        str += '<div class="d-flex gap-2">';
        str += '<p class="description">등록된 제품이 없습니다. 상품을 직접 등록하고 리뷰를 작성해보세요!</p>';
        str += '<a href="#" class="stretched-link"><i class="fa-solid fa-plus"></i></a>';
        str += '</div></div></div>';

        resultSection.append(str);
        return;
    }


    $.each(productList, function (i, item) {

        let str = '<div class="col-md-4" data-aos="fade-up" data-aos-delay="500">';
        str += '<div class="service-item d-flex position-relative h-100">';
        str += '<div>';
        str += '<h4 class="title"><a href="/product/productDetail.page?productId=' + item.productId + '" class="stretched-link productName">' + item.productName + '</a></h4>';
        str += '<p class="description productCategory">' + item.topCategory + '>' + item.subCategory + '</p>';
        str += '<p class="description">리뷰개수: ' + item.reviewCount + '</p>';
        str += '</div></div></div>';

        resultSection.append(str);
    })
}
