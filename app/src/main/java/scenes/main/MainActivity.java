package scenes.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.saba.wifidirectchat.R;

public class MainActivity extends AppCompatActivity
                          implements MainContractor.View {

    private MainContractor.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenter(this);

        presenter.start();
    }
}
