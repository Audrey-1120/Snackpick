
// 지도 띄우기
var infowindow = new kakao.maps.InfoWindow({zIndex:1});

var container = document.getElementById('map'); // 지도를 담을 영역

var options = { // 지도의 중심 좌표
    center: new kakao.maps.LatLng(37.50185785121449, 126.78766910206697), // center는 지도 생성에 반드시 필요함! 인자는 위도, 경도
    level: 3 // 지도 확대 레벨
};

// 지도 생성
var map = new kakao.maps.Map(container, options);

// 장소 검색 객체 생성
var ps = new kakao.maps.services.Places(map);

// 카테고리로 편의점 검색
ps.categorySearch('CS2', placesSearchCB, {
    location: new kakao.maps.LatLng(37.50185785121449, 126.78766910206697),
    radius: 1000,
    sort: kakao.maps.services.SortBy.DISTANCE
});

// 키워드 검색 완료 시 호출되는 콜백 함수
function placesSearchCB (data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {
        for (var i=0; i<data.length; i++) {
            displayMarker(data[i]);
        }
    }
}

// 지도에 마커 표시하는 함수
function displayMarker(place){
    let marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x)
    });

    // 마커에 클릭 이벤트 등록
    kakao.maps.event.addListener(marker, 'click', function() {
        infowindow.setContent('<div style="padding:5px;font-size:12px;">' + place.place_name + '</div>');
        infowindow.open(map, marker);
    });
}









