package cn.j1angvei.castk2.panther;

import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.SwUtil;
import okhttp3.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * using pantherdb.org to do GENE_ONTOLOGY and PATHWAY analysis
 * Created by Wayne on 3/7 2017.
 */
public class PantherAnalysis {
    private OkHttpClient okHttpClient;
    private PantherCookieJar cookieJar;
    private PantherApi pantherApi;
    private String expCode, species;
    private String geneList, outFileName;

    private PantherAnalysis(String expCode, int genomeCode, String geneList, String outFileName) {
        if (geneList == null || outFileName == null || genomeCode == 0 || expCode == null) {
            System.err.println("ERROR: check input argument!");
        }
        cookieJar = new PantherCookieJar();
        okHttpClient = initClient();
        pantherApi = initApi();
        this.expCode = expCode;
        species = SwUtil.genomeCodeToSpecies(genomeCode);
        this.geneList = geneList;
        this.outFileName = outFileName;
    }

    public static PantherAnalysis newInstance(String expCode, int genomeCode, String geneList, String outFileName) {
        if (expCode == null) {
            System.out.println("WARNING: start go analysis from solely function");
            expCode = "solely";
        }
        return new PantherAnalysis(expCode, genomeCode, geneList, outFileName);
    }

    public void analysis() {
        //check if there is network connection
        if (!isPantherAvailable()) {
            System.err.println("Can not connect to " + PantherApi.URL_BASE + " right now, try it later");
            //create empty file in case following analysis throw FileNotFound IOException
            FileUtil.createFileIfNotExist(outFileName);
            return;
        }
        //start analysis
        initCookies();
        uploadGeneList(geneList);
        FileUtil.overwriteFile("", outFileName);
        for (GoType goType : GoType.values()) {
            calculateChart(goType.getType());
            String content = exportChart(goType.getType());
            if (content == null || content.isEmpty()) {
                continue;
            }
            FileUtil.appendFile("#" + goType.getDescription(), outFileName, true);
            FileUtil.appendFile(content, outFileName, false);
        }
    }

    private void initCookies() {
        Call<String> call = pantherApi.initiate();
        try {
            Response<String> response = call.execute();
            printStatus("initCookies", response.code());
        } catch (IOException e) {
            System.err.println("initCookies failed");
        }
    }

    private void uploadGeneList(String fileName) {
        try {
            RequestBody uploadGeneBody = createUploadBody(fileName, species);
            Response<String> response = pantherApi.uploadGene(uploadGeneBody).execute();
            printStatus("uploadGeneList", response.code());
        } catch (IOException e) {
            System.err.println("ERROR: " + fileName + " not found!");
        }
    }

    private void calculateChart(int goType) {
        try {
            Response<String> response = pantherApi.calculateChart(createChartQueryMap(goType)).execute();
            printStatus("calculateChart ", response.code());
        } catch (IOException e) {
            System.err.println("calculateChart failed");
        }
    }

    private String exportChart(int type) {
        try {
            String referHeader = createChartReferer(type);
            Response<String> response = pantherApi.exportResult(referHeader).execute();
            printStatus("exportChart", response.code());
            return response.body();
        } catch (IOException e) {
            System.err.println("exportChart failed");
        }
        return null;
    }

    private RequestBody createUploadBody(String fileName, String species) {
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

    private Map<String, String> createChartQueryMap(int goType) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("listType", "1");
        queryMap.put("filterLevel", "1");
        queryMap.put("type", "" + goType);
        queryMap.put("chartType", "1");
        queryMap.put("trackingId", cookieJar.getJSessionId());
        queryMap.put("save", "yes");
        queryMap.put("basketItems", "all");
        return queryMap;
    }

    private String createChartReferer(int goType) {
        Map<String, String> queryMap = createChartQueryMap(goType);
        StringBuilder referer = new StringBuilder(PantherApi.URL_BASE + PantherApi.CHART + "?");
        for (Map.Entry<String, String> entry : queryMap.entrySet()) {
            referer.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        return referer.substring(0, referer.length() - 1);
    }

    public static boolean isPantherAvailable() {
        try {
            URL url = new URL(PantherApi.URL_BASE);
            Request request = new Request.Builder().url(url).build();
            okhttp3.Response response = new OkHttpClient().newCall(request).execute();
            int code = response.code();
            return code == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void printStatus(String job, int code) {
        String description;
        switch (code) {
            case 200:
                description = " Connection success!";
                break;
            case 400:
                description = "Local request error!";
                break;
            case 500:
                description = "Remote server error!";
                break;
            default:
                description = "Unknown error!";
        }
        System.out.printf("Job: %s\tSpecies: %s\tExperiment: %s\tStatus: %d\t%s\n", job, species, expCode, code, description);
    }

    private OkHttpClient initClient() {
        return new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(new HeaderInterceptor())
                .readTimeout(0, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    private PantherApi initApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(PantherApi.URL_BASE)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(PantherApi.class);
    }
}