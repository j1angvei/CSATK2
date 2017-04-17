package cn.j1angvei.castk2.panther;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

/**
 * panther API retrieved by Fiddler
 * Created by Wayne on 3/10 2017.
 */
public interface PantherApi {
    String URL_BASE = "http://pantherdb.org";
    String INITIATE = "/";
    String UPLOAD = "/geneListAnalysis.do";
    String CHART = "/chart/pantherChart.jsp";
    String EXPORT = "/chart/pantherChartExport.jsp";

    @GET(INITIATE)
    Call<String> initiate();

    @Headers({"Cache-Control: max-age=0", "Origin: " + URL_BASE, "Referer:" + URL_BASE,
            "Content-Type: multipart/form-data;boundary=----WebKitFormBoundaryEcgA6Z4z3AgJXQH1"})
    @POST(UPLOAD)
    Call<String> uploadGene(@Body RequestBody geneBody);

    @Headers({"Origin: " + URL_BASE, "Referer:" + URL_BASE + UPLOAD})
    @GET(CHART)
    Call<String> calculateChart(@QueryMap Map<String, String> map);

    @GET(EXPORT)
    Call<String> exportResult(@Header("Referer") String referer);

}