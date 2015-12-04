package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Security;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.AuthenticateUserService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.BeginApplicationService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;

/**
 * Created by Ana Beatriz on 27/11/2015.
 */
public class LoginActivity extends AppCompatActivity {
    private boolean loggedIn;
    private boolean running;

    private EditText etPhoneNumberLogin;
    private EditText etPasswordLogin;
    private CheckBox cbShowPasswordLogin;
    private Button bLogin;
    private TextView tvRegister;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!running) {
            beginApplication();
            running = true;
        }
        if (loggedIn) {
            showInbox();
        }

        etPhoneNumberLogin = (EditText) findViewById(R.id.etPhoneNumberLogin);
        etPasswordLogin = (EditText) findViewById(R.id.etPasswordLogin);
        cbShowPasswordLogin = (CheckBox) findViewById(R.id.cbShowPasswordLogin);

        bLogin = (Button) findViewById(R.id.bLoginConfirm);
        tvRegister = (TextView) findViewById(R.id.tvRegister);

        //define listeners
        cbShowPasswordLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPasswordLogin.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    etPasswordLogin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etPasswordLogin.setSelection(etPasswordLogin.getText().length());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void beginApplication() {
        try {
            BeginApplicationService service = new BeginApplicationService(getApplicationContext());
            service.execute();
        } catch (FailedServiceException exception) {
            Toast toast = Toast.makeText(getApplicationContext(), "Failed to begin", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void register(View view) {
        Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void login(View view) {
        try {
            String phoneNumber = etPhoneNumberLogin.getText().toString();
            String password = etPasswordLogin.getText().toString();
            AuthenticateUserService service = new AuthenticateUserService(phoneNumber, password);
            service.execute();
            loggedIn = true;
            showInbox();
        } catch (FailedServiceException exception) {
            Toast toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void showInbox() {
        Intent showInboxIntent = new Intent(getApplicationContext(), ShowInboxActivity.class);
        startActivity(showInboxIntent);
    }

    public void showAdminPanel(View view) {
        Intent adminPanelIntent = new Intent(getApplicationContext(), AdminPanelActivity.class);
        startActivity(adminPanelIntent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom()) ) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

}
