package cn.j1angvei.castk2.panther;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

/**
 * Created by Wayne on 3/10 0010.
 */
public interface PantherApi {
    String URL_BASE = "http://pantherdb.org";
    String SFX_UPLOAD = "/geneListAnalysis.do";
    String SFX_EXPORT = "/chart/pantherChartExport.jsp";
    String SFX_CHART = "/chart/pantherChart.jsp";

    @GET("/")
    Call<String> initiate();

    @Headers({"Cache-Control: max-age=0", "Origin: " + URL_BASE, "Referer:" + URL_BASE,
            "Content-Type: multipart/form-data;boundary=----WebKitFormBoundaryEcgA6Z4z3AgJXQH1"})
    @POST(SFX_UPLOAD)
    Call<String> uploadGene(@Body RequestBody geneBody);

    @Headers({"Origin: " + URL_BASE, "Referer:" + URL_BASE + SFX_UPLOAD})
    @GET(SFX_CHART)
    Call<String> calculateChart(@QueryMap Map<String, String> map);

    @GET(SFX_EXPORT)
    Call<String> exportResult(@Header("Referer") String referer);

}