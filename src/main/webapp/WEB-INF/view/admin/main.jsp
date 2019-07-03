<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>메인</title>
</head>
<style>
    .wrap {position: absolute;left: 0;bottom: 40px;width: 288px;height: 132px;margin-left: -144px;text-align: left;overflow: hidden;font-size: 12px;font-family: 'Malgun Gothic', dotum, '돋움', sans-serif;line-height: 1.5;}
    .wrap * {padding: 0;margin: 0;}
    .wrap .info {width: 286px;height: 120px;border-radius: 5px;border-bottom: 2px solid #ccc;border-right: 1px solid #ccc;overflow: hidden;background: #fff;}
    .wrap .info:nth-child(1) {border: 0;box-shadow: 0px 1px 2px #888;}
    .info .title {padding: 5px 0 0 10px;height: 30px;background: #eee;border-bottom: 1px solid #ddd;font-size: 18px;font-weight: bold;}
    .info .close {position: absolute;top: 10px;right: 10px;color: #888;width: 17px;height: 17px;background: url('http://t1.daumcdn.net/localimg/localimages/07/mapapidoc/overlay_close.png');}
    .info .close:hover {cursor: pointer;}
    .info .body {position: relative;overflow: hidden;}
    .info .desc {position: relative;margin: 13px 0 0 10px;height: 75px;}
    .desc .ellipsis {overflow: hidden;text-overflow: ellipsis;white-space: nowrap;}
    .desc .jibun {font-size: 11px;color: #888;margin-top: -2px;}
    .info:after {content: '';position: absolute;margin-left: -12px;left: 50%;bottom: 0;width: 22px;height: 12px;background: url('http://t1.daumcdn.net/localimg/localimages/07/mapapidoc/vertex_white.png')}
    .info .link {color: #5085BB;}
</style>
<body>
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="/semantic/semantic.min.css">
<script
        src="https://code.jquery.com/jquery-3.1.1.min.js"
        integrity="sha256-hVVnYaiADRTO2PzUGmuLJr8BLUSjGIZsDYGmIJLv2b8="
        crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="/semantic/semantic.min.js"></script>
<script src="/pagination/jquery.twbsPagination.js" type="text/javascript"></script>
<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=42ee9fcf1d2bd0c1eda803ea489c8313"></script>
<script src="/common.js" type="text/javascript"></script>

<div style="width: 400px;">
    <div class="ui raised very padded text segment">
        <h1>장소 검색 서비스</h1>
        <div class="ui fluid icon input">
            <input type="text" list="history" id="search_bar" placeholder="장소검색">
            <datalist id="history">
            </datalist>
            <i class="circular search link icon"></i>
        </div>

        <h3>인기검색어</h3>
        <div class="ui compact menu fluid">
            <div class="ui simple dropdown item">
                <span id="searchText">인기검색어</span>
                <div class="menu"></div>
            </div>
        </div>

        <div class="ui items"></div>

        <ul id="pagination" class="pagination-sm"></ul>
    </div>
    <div class="ui raised very padded container text segment">
        <div id="map" style="width:500px;height:400px;"></div>
    </div>
</div>

<script>
    var interval = null;
    var keywordOrder = 0;
    var keywordList = [];
    var curPage = 1;
    var totalPage = 1;
    var map = null;
    var overlay = null;
    var marker = null;
    var datas = [];

    //history 로드
    initHistory();
    initTopKeyword();
    initMap();


    function initMap(){

        var container = document.getElementById('map'); //지도를 담을 영역의 DOM 레퍼런스
        var options = { //지도를 생성할 때 필요한 기본 옵션
            center: new kakao.maps.LatLng(33.450701, 126.570667), //지도의 중심좌표.
            level: 3 //지도의 레벨(확대, 축소 정도)
        };

        map = new kakao.maps.Map(container, options); //지도 생성 및 객체 리턴
    }


    function setKeyword() {
        $('#searchText').text(keywordList[keywordOrder++].value);
        if (keywordOrder == keywordList.length) {
            keywordOrder = 0;
            stopInterval();
            initTopKeyword();
        }
    }

    function startInterval() {
        interval = setInterval(setKeyword, 5000);
    }

    function stopInterval() {
        if (interval != null) {
            clearInterval(interval);
        }
    }

    var searchOption = {
        url: '/admin/search',
        method: 'GET',
        data: {
            query: document.getElementById("search_bar").value,
            page: curPage,
            size: PAGE_SIZE
        },
        beforeSend: function (settings) {
            var keyword = document.getElementById("search_bar").value;
            if (keyword == "") {
                alert("검색어를 입력해주세요!");
                return false;
            }
            settings.data.query = keyword;
            return settings;
        },
        onSuccess: function (response, element) {
            datas = response.documents;
            initResultList(datas);
            initHistory();
        },
        onError: function (response) {
            alert("검색실패!");
        }
    };

    //검색 액션
    $('.search.link.icon')
        .api(searchOption);

    $('.search.link.icon').click(function(){
        //initPagination(datas);
    })

    function initHistory() {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState === xhr.DONE) {
                if (xhr.status === 200 || xhr.status === 201) {
                    var result = JSON.parse(xhr.responseText);
                    $("#history").empty();
                    $.each(result, function (i, item) {
                        var date = new Date(item.searchTime);
                        var dateFmt = date.getMonth() + 1 + "." + date.getDate();
                        $("#history").append($("<option>").attr('value', item.keyword).text(dateFmt));
                    });
                } else {
                    console.error(xhr.responseText);
                }
            }
        };
        xhr.open('GET', '/admin/history');
        xhr.send();
    }

    function initTopKeyword() {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState === xhr.DONE) {
                if (xhr.status === 200 || xhr.status === 201) {
                    keywordList = JSON.parse(xhr.responseText);
                    $('.ui.dropdown').dropdown('change values', keywordList);
                    startInterval();

                } else {
                    console.error(xhr.responseText);
                }
            }
        };
        xhr.open('GET', '/admin/topKeyword');
        xhr.send();
    }

    function initResultList(datas) {
        $('.ui.items').empty();
        $('.ui.items').append('<h3>검색결과</h3>');
        for (var idx in datas) {
            addItem(datas[idx]);
        }
    }

    function addItem(data) {
        var html = '<div class="item">' +
            '<h2 class="ui header">' +
            '<div class="content"> ' + data.place_name +
            '<div class="sub header">' +
            '<i class="map marker alternate icon">' + '</i>' + data.road_address_name +
            '<div class="description">&emsp; (지번 : ' + data.address_name + ')</div>' +
            '</div>' +

            '<div class="sub header">' +
            '<i class="icon phone">' + '</i>' + (data.phone ? data.phone : '정보없음') +
            '</div>' +

            '<a class="sub header" href="' + data.place_url + '" target="_blank">' +
            '<i class="icon linkify">' + '</i>' + '상세정보' +
            '</a>' +

            '<a class="sub header" href=https://map.kakao.com/link/map/"' + data.id + '" target="_blank">' +
            '<i class="icon map">' + '</i>' + 'Kakao Map 바로가기' +
            '</a>' +

            '<a class="sub header" onclick="setMarkToMap('+data.id+',\''+data.place_name+'\',\''+data.road_address_name+'\',\''+data.address_name+'\',\''+data.phone+'\','+data.y+','+data.x+')">' +
            '<i class="crosshairs icon" >' + '</i>' + '현재 Map에서 이동하기' +
            '</a>' +

            '</div>' +
            '</h2>' +
            '</div>'+
            '<div class="ui divider"></div>';
        $('.ui.items').append(html);
    }

    function initPagination(response){
        console.log(response)
        totalPage = response.meta.pageable_count;
        $('#pagination').twbsPagination('destroy');
        $('#pagination').twbsPagination({
            totalPages: getPage(totalPage),
            first: '<<',
            prev: '<',
            next: '>',
            last: '>>',
            onPageClick: function (evt, page) {
                searchOption.data.page=page;
                $('.search.link.icon').api('add url data','/admin/search',searchOption.data);
                $('.search.link.icon').api('query');
            }
        });
    }

    function getPage(count){
        return count/PAGE_SIZE+1;
    }

    function setMarkToMap(id, place_name, road_address, address, phone, lat, lng){
        console.log(id);
        var detail = 'https://map.kakao.com/link/map/'+id;

        panTo(lat, lng);
        drawMarker(lat, lng);
        initOverlayCon(place_name, road_address, address, phone, detail);
    }

    function panTo(lat, lng) {
        // 이동할 위도 경도 위치를 생성합니다
        var moveLatLon = new kakao.maps.LatLng(lat, lng);

        // 지도 중심을 부드럽게 이동시킵니다
        // 만약 이동할 거리가 지도 화면보다 크면 부드러운 효과 없이 이동합니다
        map.panTo(moveLatLon);
    }

    function initOverlayCon(place_name, road_address, address, phone, detail){
        var content = '<div class="wrap">' +
            '    <div class="info">' +
            '        <div class="title">' +
                        place_name +
            '            <div class="close" onclick="closeOverlay()" title="닫기"></div>' +
            '        </div>' +
            '        <div class="body">' +
            '            <div class="desc">' +
            '                <div class="ellipsis">'+road_address+'</div>' +
            '                <div class="jibun ellipsis">(지번) '+address+'</div>' +
            '                <div class="ellipsis">'+phone+'</div>' +
            '                <div><a href='+detail+' target="_blank" class="link">상세정보</a></div>' +
            '            </div>' +
            '        </div>' +
            '    </div>' +
            '</div>';

        initOverlay(content);
    }

    // 지도에 마커를 표시합니다
    function drawMarker(lan, lng){
        marker = new kakao.maps.Marker({
            map: map,
            position: new kakao.maps.LatLng(lan, lng)
        });
    }

    // 마커 위에 커스텀오버레이를 표시합니다
    // 마커를 중심으로 커스텀 오버레이를 표시하기위해 CSS를 이용해 위치를 설정했습니다
    function initOverlay(content){
        overlay = new kakao.maps.CustomOverlay({
            content: content,
            map: map,
            position: marker.getPosition()
        });

        // 마커를 클릭했을 때 커스텀 오버레이를 표시합니다
        kakao.maps.event.addListener(marker, 'click', function() {
            overlay.setMap(map);
        });
    }



    // 커스텀 오버레이를 닫기 위해 호출되는 함수입니다
    function closeOverlay() {
        overlay.setMap(null);
    }

</script>
</body>
</html>


