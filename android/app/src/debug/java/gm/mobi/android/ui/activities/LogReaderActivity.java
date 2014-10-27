package gm.mobi.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import gm.mobi.android.BuildConfig;
import gm.mobi.android.R;
import gm.mobi.android.util.FileLogger;

public class LogReaderActivity extends ActionBarActivity {

    private TextView logView;
    private String log;
    private FileLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_reader);
        if (!BuildConfig.USE_FILE_LOGGER) {
            Toast.makeText(this, "El logger de desarrollo no está activado", Toast.LENGTH_SHORT).show();
            finish();
        }
        logView = (TextView) findViewById(R.id.log_text);
        logger = FileLogger.getInstance();

        loadLogs();
    }

    private void loadLogs() {
        // Read logs
        log = logger.readLog();
        logView.setText(log);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_borrar:
                logger.deleteLogFile();
                loadLogs();
                return true;
            case R.id.action_compartir:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("editTextView/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Log de Goles");
                i.putExtra(Intent.EXTRA_TEXT, log);
                startActivity(Intent.createChooser(i, "Compartir log"));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
