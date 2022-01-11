package ddwu.mobile.finalproject.ma02_20191003;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Hospital extends AppCompatActivity {
    public static final String TAG = "Hospital";

    EditText etHosName;
    ListView hosListView;
    ImageView hImg;

    String basicApiUrl;

    ArrayList<HosInfoDto> resultList;
    HosAdapter basicAdapter;

    HosBasicParser parser;
    NetworkManager networkManager;

    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hospital);

        etHosName = findViewById(R.id.etHosName);
        hosListView = findViewById(R.id.medicListView);
        hImg = findViewById(R.id.hImg);

        basicApiUrl = getResources().getString(R.string.basic_url);

        resultList = new ArrayList();
        basicAdapter = new HosAdapter(this, R.layout.listview_layout, resultList);
        hosListView.setAdapter(basicAdapter);

        parser = new HosBasicParser();
        networkManager = new NetworkManager(this);

        // 항목 클릭 시 상세보기
        hosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HosInfoDto hid = resultList.get(position);
                Intent intent = new Intent(Hospital.this, HosDetailActivity.class);
                intent.putExtra("hid", hid);
                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnFindHos:
                hImg.setVisibility(hImg.GONE);
                query = etHosName.getText().toString();
                try {
                    new BasicAsyncTask().execute(basicApiUrl + URLEncoder.encode(query, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    class BasicAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(Hospital.this, "Wait", "Finding...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String result;
            String address = strings[0];
            // networking
            result = networkManager.downloadContents(address);
            if (result == null) return "Error!";

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // parsing
            resultList = parser.parse(result);
            basicAdapter.setList(resultList); // Adapter에 결과 List를 설정 후 notify
            progressDlg.dismiss();

        }
    }

}