package com.pitecan.slime;

import android.util.Log;
import org.json.*;
import java.io.*; 
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

class GoogleIME {
    static String[] ime(String q){
        // Google CGI API for Japanese Input
        // // http://www.google.co.jp/ime/cgiapi.html
        // // Google日本語入力のURLは "http://google.co.jp/transliterate?langpair=ja-Hira|ja&text=かんじ" のような形式だが
        // // "|" を "%7c" にしておかないと new HttpGet() が失敗する

         String urlstr = "https://google.co.jp/transliterate?langpair=ja-Hira%7cja&text="+q;
         String[] suggestions;
         final int maxSuggestions = 20;
         suggestions = new String[maxSuggestions+1];
         int nsuggest = 0;
        
         String jsonText = "[[\"\",[]]]";

         Log.d("Slime", "GoogleIME----------------------");
         Log.d("Slime", urlstr);

         try {
             Log.v("Slime","====================!!!");
             
             URL url = new URL(urlstr);
             HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
             
             BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
             String body = reader.readLine(); // 1行だけ取得
             Log.v("Slime","body = "+body);
             reader.close();
             con.disconnect();
             
             // "ここではきものをぬぐ" のようなパタンを与えたとき、
             // Google日本語入力は以下のようなJSONテキストを返す
             // [
             //   ["ここでは",
             //     ["ここでは", "個々では", "此処では"],
             //   ],
             //   ["きものを",
             //     ["着物を", "きものを", "キモノを"],
             //   ],
             //   ["ぬぐ",
             //     ["脱ぐ", "ぬぐ", "ヌグ"],
             //   ],
             // ]
             // これを読んで適当に候補を生成する
             JSONArray ja1, ja2, ja3;
             int len1, len3;
             ja1 = new JSONArray(body);
             len1 = ja1.length();
             int i = 0;
             ja2 = ja1.getJSONArray(i);
             ja3 = ja2.getJSONArray(1); // 第2要素 = 変換候補
             len3 = ja3.length();
             for(nsuggest=0; nsuggest<len3 && nsuggest < maxSuggestions; nsuggest++){
                 suggestions[nsuggest] = ja3.getString(nsuggest);
             }
             suggestions[nsuggest] = "";
             for(i=1; i<len1; i++){
                 ja2 = ja1.getJSONArray(i);
                 // String s = ja2.getString(0); // 第1要素 = 元の文字列
                 ja3 = ja2.getJSONArray(1); // 第2要素 = 変換候補
                 for(int j=0; j<nsuggest; j++){
                     suggestions[j] += ja3.getString(0); // ふたつめ以降は最初の候補を連結する
                 }
             }
             
         } catch (Exception e) {
             //Log.d("HttpSampleActivity", "Error Execute");
             e.printStackTrace();
         }
         return suggestions;
    }
}
