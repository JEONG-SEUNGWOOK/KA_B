package com.example.kaBank.services.Controller.admin;

import com.example.kaBank.services.DTO.HistoryDTO;
import com.example.kaBank.services.Repository.HistoryRepository;
import com.example.kaBank.util.ServerPaging;

import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class WebController {

    private HistoryRepository historyRepository;

    @GetMapping("/main")
    public String login(){
        return "admin/main";
    }

    @ResponseBody
    @GetMapping("search")
    public ServerPaging<Object> search(@RequestParam(value="query") String query,
                                          @RequestParam(value="page", defaultValue = "1") int page,
                                          @RequestParam(value="size", defaultValue = "10") int size){
        String result = "";
        Map<String, Object> datas = new HashMap<>();
        String queryString = "query="+query+"&page="+page+"&size="+size;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://dapi.kakao.com/v2/local/search/keyword.json?"+queryString)
                .get()
                .addHeader("Authorization", "KakaoAK 42ee9fcf1d2bd0c1eda803ea489c8313")
                .build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();

            //History 카운트증가
            HistoryDTO historyDTO = historyRepository.findById(query).orElse(null);
            if(historyDTO == null){
                historyDTO = new HistoryDTO();
                historyDTO.setKeyword(query);
            }
            else {
                historyDTO.setCount(historyDTO.getCount()+1);
                historyDTO.setSearchTime(LocalDateTime.now());
            }
            historyRepository.save(historyDTO);

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject(result);
        Integer totalPage = Integer.parseInt(String.valueOf(obj.getJSONObject("meta").get("pageable_count")));

        List<Object> arr = obj.getJSONArray("documents").toList();
        System.out.println(arr);

        ServerPaging<Object> serverPaging = new ServerPaging();
        serverPaging.setTotal(totalPage);
        serverPaging.setResults(arr);
        return serverPaging;
    }

    @ResponseBody
    @GetMapping("history")
    public List<HistoryDTO> getHistory(){
        List<HistoryDTO> list = historyRepository.findAllByOrderBySearchTimeDesc();
        return list;

    }


    @ResponseBody
    @GetMapping("topKeyword")
    public List<Map> getTop10Keyword(){
        List<Map> list = historyRepository.findTop10ByOrderByCountDesc();
        list = keyChangeLower(list);
        return list;

    }

    private static List keyChangeLower(List<Map> list) {

        List<Map> newList = new LinkedList<Map>();

        for (int i = 0; i < list.size(); i++) {

            HashMap<String, String> tm = new HashMap<String, String>(list.get(i));

            Iterator<String> iteratorKey = tm.keySet().iterator(); // 키값 오름차순

            Map newMap = new HashMap();

            // 키값 내림차순 정렬
            while (iteratorKey.hasNext()) {

                String key = iteratorKey.next();
                String value = String.format("%d. %s", i + 1, tm.get(key));
                newMap .put(key.toLowerCase(), value);

            }

            newList.add(newMap);

        }

        return newList;

    }



}
