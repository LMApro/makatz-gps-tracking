package example.makatz.gpstracking1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by makatz on 3/12/2016.
 */
public class InfoActivity extends AppCompatActivity {
    private TextView txtAppDescription, txtAuthorName, txtAuthorEmail, txtAuthorPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        txtAppDescription = (TextView) findViewById(R.id.info_app_description);
        txtAuthorName = (TextView) findViewById(R.id.info_author_name);
        txtAuthorEmail = (TextView) findViewById(R.id.info_author_email);
        txtAuthorPhone = (TextView) findViewById(R.id.info_author_phone);

        txtAuthorName.setText(getString(R.string.info_author_name, "Lâm Minh Anh"));
        txtAuthorEmail.setText(getString(R.string.info_author_email, "matn95@gmail.com"));
        txtAuthorPhone.setText(getString(R.string.info_author_phone, "0989.0913.09"));
    }
}
