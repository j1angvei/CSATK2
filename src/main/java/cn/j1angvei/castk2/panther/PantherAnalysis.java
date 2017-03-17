package cn.j1angvei.castk2.panther;

import cn.j1angvei.castk2.util.FileUtil;
import cn.j1angvei.castk2.util.SwUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * using pantherdb.org to do GO_PATHWAY and KEGG analysis
 * Created by Wayne on 3/7 2017.
 */
public class PantherAnalysis {
    private PantherCookieJar mCookieJar;
    private PantherApi mPantherApi;
    private String mExpCode, mSpecies;
    private String mGeneList, mOutFileName;

    private PantherAnalysis(String expCode, int genomeCode, String geneList, String outFileName) {
        if (geneList == null || outFileName == null || genomeCode == 0 || expCode == null) {
            System.err.println("ERROR: check input argument!");
        }
        mCookieJar = new PantherCookieJar();
        mPantherApi = initApi();
        mExpCode = expCode;
        mSpecies = SwUtil.genomeCodeToSpecies(genomeCode);
        mGeneList = geneList;
        mOutFileName = outFileName;
    }

    public static PantherAnalysis newInstance(String expCode, int genomeCode, String geneList, String outFileName) {
        if (expCode == null) {
            System.out.println("WARNING: run go analysis from solely function");
            expCode = "solely";
        }
        return new PantherAnalysis(expCode, genomeCode, geneList, outFileName);
    }

    public void analysis() {
        initCookies();
        uploadGeneList(mGeneList);
        FileUtil.overwriteFile("", mOutFileName);
        for (GoType goType : GoType.values()) {
            calculateChart(goType.getType());
            String content = exportChart(goType.getType());
            if (content == null || content.isEmpty()) {
                continue;
            }
            String modified = addColumn(goType.getDescription(), content);
            FileUtil.appendFile(modified, mOutFileName);
        }
    }

    private String addColumn(String goType, String originalContent) {
        String modified = "";
        for (String line : originalContent.split("\n")) {
            modified += line.replaceFirst("^", mExpCode + "\t" + goType + "\t");
            modified += "\n";
        }
        return modified;
    }

    private void initCookies() {
        Call<String> call = mPantherApi.initiate();
        try {
            Response<String> response = call.execute();
            printStatus("initCookies", response.code());
        } catch (IOException e) {
            System.err.println("initCookies failed");
        }
    }

    private void uploadGeneList(String fileName) {
        try {
            RequestBody uploadGeneBody = createUploadBody(fileName, mSpecies);
            Response<String> response = mPantherApi.uploadGene(uploadGeneBody).execute();
            printStatus("uploadGeneList", response.code());
        } catch (IOException e) {
            System.err.println("ERROR: " + fileName + " not found!");
        }
    }

    private void calculateChart(int goType) {
        try {
            Response<String> response = mPantherApi.calculateChart(createChartQueryMap(goType)).execute();
            printStatus("calculateChart ", response.code());
        } catch (IOException e) {
            System.err.println("calculateChart failed");
        }
    }

    private String exportChart(int type) {
        try {
            String referHeader = createChartReferer(type);
            Response<String> response = mPantherApi.exportResult(referHeader).execute();
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
        queryMap.put("trackingId", mCookieJar.getJSessionId());
        queryMap.put("save", "yes");
        queryMap.put("basketItems", "all");
        return queryMap;
    }

    private String createChartReferer(int goType) {
        Map<String, String> queryMap = createChartQueryMap(goType);
        String referer = PantherApi.URL_BASE + PantherApi.SFX_CHART + "?";
        for (Map.Entry<String, String> entry : queryMap.entrySet()) {
            referer += entry.getKey() + "=" + entry.getValue() + "&";
        }
        return referer.substring(0, referer.length() - 1);
    }

    private void printStatus(String job, int code) {
        String description = mSpecies + "\t" + mExpCode + "\t";
        switch (code) {
            case 200:
                description += " connection success!";
                break;
            case 400:
                description += "local request error!";
                break;
            case 500:
                description += "remote server error!";
                break;
            default:
                description += "unknown error!";
        }
        System.out.println(job + "\t" + description);
    }

    private PantherApi initApi() {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(mCookieJar)
                .addInterceptor(new HeaderInterceptor())
                .readTimeout(0, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(PantherApi.URL_BASE)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(PantherApi.class);
    }
}