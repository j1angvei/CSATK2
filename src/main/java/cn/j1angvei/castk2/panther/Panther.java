package cn.j1angvei.castk2.panther;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.*;
import retrofit2.http.Headers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * using pantherdb.org to do GO, PATHWAY and KEGG analysis
 * Created by Wayne on 3/7 0007.
 */
public class Panther {
    private static final String URL_BASE = "http://pantherdb.org";
    private static final String URL_SFX_UPLOAD = "/geneListAnalysis.do";
    private static final String URL_SFX_EXPORT = "/chart/pantherChartExport.jsp";
    private static final String URL_SFX_CHART = "/chart/pantherChart.jsp";

    private static Api getApi() {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(PantherCookieJar.getInstance())
                .addInterceptor(new AddHeaderInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(URL_BASE)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(Api.class);
    }

    public static void analysis(String geneList, String outFile, int species) {
        if (geneList == null || outFile == null || species == 0) {
            System.err.println("ERROR: check input argument!");
        }
        initCookies();
        uploadGeneList();
        for (AnalysisType type : AnalysisType.values()) {
            System.out.println(type.getInitial());
            calculateChart(type.getType());
            exportChart(type.getType());
        }
    }

    public static void initCookies() {
        try {
            Response<String> response = getApi().initiate().execute();
            System.out.println(response.code());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadGeneList() {
        try {
            Response<String> response = getApi().uploadGene(createUploadBody("input.bed", "Homo sapiens")).execute();
            System.out.println(response.code());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void calculateChart(int type) {
        try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("listType", "1");
            queryMap.put("filterLevel", "1");
            queryMap.put("type", String.valueOf(type));
            queryMap.put("chartType", "1");
            queryMap.put("trackingId", PantherCookieJar.getInstance().getJSessionId());
            queryMap.put("save", "yes");
            queryMap.put("basketItems", "all");
            Response<String> response = getApi().calculateChart(queryMap).execute();
            System.out.println(response.code() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportChart(int type) {
        try {
            String referHeader = URL_BASE + URL_SFX_CHART + createChartUrlSuffix(type, PantherCookieJar.getInstance().getJSessionId());
            Response<String> type1 = getApi().exportResult(referHeader).execute();
            System.out.println(type1.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface Api {
        @GET("/")
        Call<String> initiate();

        @Headers({"Cache-Control: max-age=0", "Origin: " + URL_BASE, "Referer:" + URL_BASE,
                "Content-Type: multipart/form-data;boundary=----WebKitFormBoundaryEcgA6Z4z3AgJXQH1"})
        @POST(URL_SFX_UPLOAD)
        Call<String> uploadGene(@Body RequestBody geneBody);

        @Headers({"Origin: " + URL_BASE, "Referer:" + URL_BASE + URL_SFX_UPLOAD})
        @GET(URL_SFX_CHART)
        Call<String> calculateChart(@QueryMap Map<String, String> map);

        @GET(URL_SFX_EXPORT)
        Call<String> exportResult(@Header("Referer") String referer);

    }

    private static RequestBody createUploadBody(String fileName, String species) {
        File geneListFile = new File(fileName);
        return new MultipartBody.Builder("----WebKitFormBoundaryEcgA6Z4z3AgJXQH1")
                .setType(MultipartBody.FORM)
                .addFormDataPart("idField", "")
                .addFormDataPart("fileData", fileName, RequestBody.create(MediaType.parse("application/octet-stream"), geneListFile))
                .addFormDataPart("fileType", "10")
                .addFormDataPart("organism", species)
                .addFormDataPart("dataset", species)
                .addFormDataPart("resultType", "1")
                .build();
    }

    private static String createChartUrlSuffix(int type, String sessionId) {
        return "?listType=1" +
                "&filterLevel=1" +
                "&type=" + type +
                "&chartType=1" +
                "&trackingId=" + sessionId +
                "&save=yes" +
                "&basketItems=all";
    }

    private enum AnalysisType {
        MOLECULAR_FUNCTION(1, "MF"),
        BIOLOGICAL_PROCESS(2, "BP"),
        PATHWAY(3, "KEGG"),
        CELLULAR_COMPONENT(4, "CC");
        private int type;
        private String initial;

        AnalysisType(int type, String initial) {
            this.type = type;
            this.initial = initial;
        }

        public int getType() {
            return type;
        }

        public String getInitial() {
            return initial;
        }
    }
}
