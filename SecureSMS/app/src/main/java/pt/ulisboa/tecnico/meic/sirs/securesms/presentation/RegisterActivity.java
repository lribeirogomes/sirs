package pt.ulisboa.tecnico.meic.sirs.securesms.presentation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.rustamg.filedialogs.FileDialog;
import com.rustamg.filedialogs.OpenFileDialog;

import java.io.File;

import pt.ulisboa.tecnico.meic.sirs.securesms.R;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.CreateUserService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ImportCACertificateService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ImportPrivateKeysService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.ImportUserCertificateService;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.FailedServiceException;
import pt.ulisboa.tecnico.meic.sirs.securesms.service.exceptions.SecureSmsException;

/**
 * Created by Ana Beatriz on 27/11/2015.
 */
public class RegisterActivity extends AppCompatActivity implements FileDialog.OnFileSelectedListener {
    private enum ImportFileType {
        CA_CERTIFICATE,
        USER_CERTIFICATE,
        PRIVATE_RSA_KEY,
        PRIVATE_EC_KEY,
    }

    private float lastX;
    private ViewFlipper viewFlipper;
    private EditText etPhoneNumberRegister;
    private EditText etPasswordRegister;
    private EditText etCACertificate;
    private EditText etUserCertificate;
    private EditText etPasswordKeys;
    private EditText etPrivateRsaKey;
    private EditText etPrivateEcKey;
    private TextView tvCACertificateValid;
    private TextView tvCACertificateInvalid;
    private TextView tvUserCertificateValid;
    private TextView tvUserCertificateInvalid;
    private TextView tvPrivateRsaKeyValid;
    private TextView tvPrivateRsaKeyInvalid;
    private TextView tvPrivateEcKeyValid;
    private TextView tvPrivateEcKeyInvalid;
    private ImageButton bCACertificate;
    private ImageButton bUserCertificate;
    private ImageButton bPrivateRsaKey;
    private ImageButton bPrivateEcKey;
    private Button bBackRegister1;
    private Button bNextRegister1;
    private Button bBackRegister2;
    private Button bNextRegister2;
    private Button bBackRegister3;
    private Button bNextRegister3;
    private CheckBox cbSelfSigned;
    private CheckBox cbShowPasswordRegister;
    private FloatingActionButton fabRegister1;
    private FloatingActionButton fabRegister2;
    private FloatingActionButton fabRegister3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setItems();
        //desperate hammer, sorry
        showNext();
        showPrevious();
        fabRegister2.setScaleX(0.7f);
        fabRegister2.setScaleY(0.7f);
        fabRegister3.setScaleX(0.7f);
        fabRegister3.setScaleY(0.7f);
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

    private void setItems() {
        fabRegister1 = (FloatingActionButton) findViewById(R.id.fabRegister1);
        fabRegister2 = (FloatingActionButton) findViewById(R.id.fabRegister2);
        fabRegister3 = (FloatingActionButton) findViewById(R.id.fabRegister3);
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        //step1
        View vStep1 = LayoutInflater.from(this).inflate(R.layout.activity_register1, viewFlipper, true);
        etPhoneNumberRegister = (EditText) vStep1.findViewById(R.id.etPhoneNumberRegister);
        etPhoneNumberRegister.addTextChangedListener(textWatcherRegister1());

        etPasswordRegister = (EditText) vStep1.findViewById(R.id.etPasswordRegister);
        etPasswordRegister.addTextChangedListener(textWatcherRegister1());

        cbShowPasswordRegister = (CheckBox) vStep1.findViewById(R.id.cbShowPasswordRegister);
        cbShowPasswordRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etPasswordRegister.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    etPasswordRegister.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etPasswordRegister.setSelection(etPasswordRegister.getText().length());
            }
        });

        bBackRegister1 = (Button) vStep1.findViewById(R.id.bBackRegister1);
        bNextRegister1 = (Button) vStep1.findViewById(R.id.bNextRegister1);

        //step2
        View vStep2 = LayoutInflater.from(this).inflate(R.layout.activity_register2, viewFlipper, true);

        tvCACertificateValid = (TextView) vStep2.findViewById(R.id.tvCACertificateValid);
        tvCACertificateInvalid = (TextView) vStep2.findViewById(R.id.tvCACertificateInvalid);
        etCACertificate = (EditText) vStep2.findViewById(R.id.etCACertificate);
        etCACertificate.setKeyListener(null);

        bCACertificate = (ImageButton) vStep2.findViewById(R.id.bCACertificate);
        bCACertificate.setOnClickListener(customOnClickListener(ImportFileType.CA_CERTIFICATE));

        tvUserCertificateValid = (TextView) vStep2.findViewById(R.id.tvUserCertificateValid);
        tvUserCertificateInvalid = (TextView) vStep2.findViewById(R.id.tvUserCertificateInvalid);
        etUserCertificate = (EditText) vStep2.findViewById(R.id.etUserCertificate);
        etUserCertificate.setKeyListener(null);
        etUserCertificate.addTextChangedListener(textWatcherRegister2());

        bUserCertificate = (ImageButton) vStep2.findViewById(R.id.bUserCertificate);
        bUserCertificate.setOnClickListener(customOnClickListener(ImportFileType.USER_CERTIFICATE));
        bUserCertificate.setEnabled(false);
        cbSelfSigned = (CheckBox) findViewById(R.id.cbSelfSigned);

        bBackRegister2 = (Button) vStep2.findViewById(R.id.bBackRegister2);
        bNextRegister2 = (Button) vStep2.findViewById(R.id.bNextRegister2);

        //step3
        View vStep3 = LayoutInflater.from(this).inflate(R.layout.activity_register3, viewFlipper, true);
        etPasswordKeys = (EditText) vStep3.findViewById(R.id.etPasswordKeys);
        etPasswordKeys.addTextChangedListener(textWatcherRegister3Password());

        tvPrivateRsaKeyValid = (TextView) vStep3.findViewById(R.id.tvPrivateRsaKeyValid);
        tvPrivateRsaKeyInvalid = (TextView) vStep3.findViewById(R.id.tvPrivateRsaKeyInvalid);
        etPrivateRsaKey = (EditText) vStep3.findViewById(R.id.etPrivateRsaKey);
        etPrivateRsaKey.setKeyListener(null);
        etPrivateRsaKey.addTextChangedListener(textWatcherRegister3());

        bPrivateRsaKey = (ImageButton) vStep3.findViewById(R.id.bPrivateRsaKey);
        bPrivateRsaKey.setOnClickListener(customOnClickListener(ImportFileType.PRIVATE_RSA_KEY));
        bPrivateRsaKey.setEnabled(false);

        tvPrivateEcKeyValid = (TextView) vStep3.findViewById(R.id.tvPrivateEcKeyValid);
        tvPrivateEcKeyInvalid = (TextView) vStep3.findViewById(R.id.tvPrivateEcKeyInvalid);
        etPrivateEcKey = (EditText) vStep3.findViewById(R.id.etPrivateEcKey);
        etPrivateEcKey.setKeyListener(null);
        etPrivateEcKey.addTextChangedListener(textWatcherRegister3());

        bPrivateEcKey = (ImageButton) vStep3.findViewById(R.id.bPrivateEcKey);
        bPrivateEcKey.setOnClickListener(customOnClickListener(ImportFileType.PRIVATE_EC_KEY));
        bPrivateEcKey.setEnabled(false);


        bBackRegister3 = (Button) vStep3.findViewById(R.id.bBackRegister3);
        bNextRegister3 = (Button) vStep3.findViewById(R.id.bNextRegister3);

    }

    //custom listeners
    private TextWatcher textWatcherRegister1() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfRegister1Ready();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void enableSubmitIfRegister1Ready() {
        if (
                etPhoneNumberRegister.getText().toString().length() < 12 ||
                etPasswordRegister.getText().toString().length() < 7) {
            bNextRegister1.setEnabled(false);
            fabRegister2.setOnClickListener(disableFab());
            fabRegister3.setOnClickListener(disableFab());
        }
        else {
            bNextRegister1.setEnabled(true);
            fabRegister2.setOnClickListener(enableFab(fabRegister2));
            fabRegister3.setOnClickListener(disableFab());
        }
    }


    private TextWatcher textWatcherRegister2() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfRegister2Ready();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void enableSubmitIfRegister2Ready() {
        if (
                tvUserCertificateValid.getVisibility() == View.INVISIBLE) {
            bNextRegister2.setEnabled(false);
            fabRegister3.setOnClickListener(disableFab());
        } else {
            bNextRegister2.setEnabled(true);
            fabRegister3.setOnClickListener(enableFab(fabRegister3));
        }
    }


    private TextWatcher textWatcherRegister3() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfRegister3Ready();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void enableSubmitIfRegister3Ready() {
        if (
                tvPrivateEcKeyValid.getVisibility() == View.INVISIBLE || tvPrivateRsaKeyValid.getVisibility() == View.INVISIBLE) {
            bNextRegister3.setEnabled(false);
        }
        else {
            bNextRegister3.setEnabled(true);
        }
    }

    private TextWatcher textWatcherRegister3Password() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfRegister3PasswordReady();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void enableSubmitIfRegister3PasswordReady() {
        if (etPasswordKeys.getText().toString().length()<7) {
            bPrivateRsaKey.setEnabled(false);
            bPrivateEcKey.setEnabled(false);
        }
        else {
            bPrivateRsaKey.setEnabled(true);
            bPrivateEcKey.setEnabled(true);
        }
    }

    //fab
    private View.OnClickListener disableFab() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        };
    }

    private View.OnClickListener enableFab(final FloatingActionButton fab) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (fab.getId()) {
                    case R.id.fabRegister1: {
                        showStep1(v);
                        break;
                    }
                    case R.id.fabRegister2: {
                        showStep2(v);
                        break;
                    }
                    case R.id.fabRegister3: {
                        showStep3(v);
                        break;
                    }

                }
            }
        };
    }


    //swap animation
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            // when user first touches the screen to swap
            case MotionEvent.ACTION_DOWN: {
                lastX = touchevent.getX();
                break;
            }
            case MotionEvent.ACTION_UP: {
                float currentX = touchevent.getX();

                // if left to right swipe on screen
                if (lastX < currentX) {
                    // If no more View/Child to flip
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;
                    showPrevious();
                }
                // if right to left swipe on screen
                if (lastX > currentX) {
                    if (viewFlipper.getDisplayedChild() == 2)
                        break;
                    showNext();
                }
                break;
            }
        }
        return false;
    }

    public void showNext(View view) {
        showNext();
    }

    private void showNext() {
        // The Next screen will come in form Left and current Screen will go OUT from Right
        viewFlipper.setInAnimation(this, R.anim.in_from_right);
        viewFlipper.setOutAnimation(this, R.anim.out_to_left);
        // Show the next Screen
        popDownFab();
        viewFlipper.showNext();
        updateFab();
    }

    public void showPrevious(View view) {
        showPrevious();
    }

    private void showPrevious() {
        // The Next screen will come in form Right and current Screen will go OUT from Left
        viewFlipper.setInAnimation(this, R.anim.in_from_left);
        viewFlipper.setOutAnimation(this, R.anim.out_to_right);
        // Show The Previous Screen
        popDownFab();
        viewFlipper.showPrevious();
        updateFab();
    }

    //fab pop animation
    private void popDown(FloatingActionButton fab) {
        //fab.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.pop_down);
        fab.startAnimation(animation);
    }

    private void popUp(FloatingActionButton fab) {
        //fab.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        fab.startAnimation(animation);
    }

    //update screens
    private void popDownFab() {
        switch (viewFlipper.getDisplayedChild()) {
            case 0: {
                popDown(fabRegister1);
                break;
            }
            case 1: {
                popDown(fabRegister2);
                break;
            }
            case 2: {
                popDown(fabRegister3);
                break;
            }
        }
    }

    private ColorStateList getColorById(int colorId) {
        return ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), colorId));
    }

    private void updateFab() {
        switch (viewFlipper.getDisplayedChild()) {
            case 0: {
                fabRegister1.setBackgroundTintList(getColorById(R.color.colorAccent));
                fabRegister2.setBackgroundTintList(getColorById(R.color.colorBackground));
                fabRegister3.setBackgroundTintList(getColorById(R.color.colorBackground));
                popUp(fabRegister1);
                break;
            }
            case 1: {
                fabRegister1.setBackgroundTintList(getColorById(R.color.colorAccent));
                fabRegister2.setBackgroundTintList(getColorById(R.color.colorAccent));
                fabRegister3.setBackgroundTintList(getColorById(R.color.colorBackground));

                fabRegister2.setScaleX(1.0f);
                fabRegister2.setScaleY(1.0f);
                popUp(fabRegister2);
                break;
            }
            case 2: {
                fabRegister1.setBackgroundTintList(getColorById(R.color.colorAccent));
                fabRegister2.setBackgroundTintList(getColorById(R.color.colorAccent));
                fabRegister3.setBackgroundTintList(getColorById(R.color.colorAccent));
                fabRegister3.setScaleX(1.0f);
                fabRegister3.setScaleY(1.0f);
                popUp(fabRegister3);
                break;
            }
        }
    }

    public void showStep1(View view) {
        if (viewFlipper.getDisplayedChild() == 1) {
            showPrevious();
        } else if (viewFlipper.getDisplayedChild() == 2) {
            showPrevious();
            showPrevious();
        }
    }

    public void showStep2(View view) {
        if (viewFlipper.getDisplayedChild() == 0) {
            showNext();
        } else if (viewFlipper.getDisplayedChild() == 2) {
            showPrevious();
        }
    }

    public void showStep3(View view) {
        if (viewFlipper.getDisplayedChild() == 0) {
            showNext();
            showNext();
        } else if (viewFlipper.getDisplayedChild() == 1) {
            showNext();
        }
    }


    public void cancelRegistration(View view) {
        finish();
    }

    public void finishRegistration(View view) {
        Toast toast;
        try {
            String phoneNumber = etPhoneNumberRegister.getText().toString();
            String password = etPasswordRegister.getText().toString();
            CreateUserService service = new CreateUserService(phoneNumber, password);
            service.execute();

            toast = Toast.makeText(getApplicationContext(), "Setup successful", Toast.LENGTH_SHORT);
        } catch (FailedServiceException exception) {
            toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT);
        }
        toast.show();
        finish();
    }


//file explorer stuff

    @Override
    public void onFileSelected(FileDialog dialog, File file) {
        ImportFileType type = ImportFileType.values()[dialog.getArguments().getInt("type")];
        String userPassword = etPasswordRegister.getText().toString();
        String keysPassword = etPasswordKeys.getText().toString();
        switch (type) {
            case CA_CERTIFICATE: {
                try {
                    ImportCACertificateService service = new ImportCACertificateService(file.getPath(), userPassword);
                    service.execute();
                    //valid certificate
                    etCACertificate.setText(file.getPath());
                    tvCACertificateValid.setVisibility(View.VISIBLE);
                    tvCACertificateInvalid.setVisibility(View.INVISIBLE);
                    bUserCertificate.setEnabled(true);
                } catch (SecureSmsException exception) {
                    //invalid certificate
                    tvCACertificateValid.setVisibility(View.INVISIBLE);
                    tvCACertificateInvalid.setVisibility(View.VISIBLE);
                    bUserCertificate.setEnabled(false);
                } finally {
                    etCACertificate.setText(file.getPath());
                }
                break;
            }
            case USER_CERTIFICATE: {
                try {
                    ImportUserCertificateService service = new ImportUserCertificateService(file.getPath(), true, userPassword);
                    service.execute();
                    if (!service.getResult()) {
                        //prompt user to accept
                        service = new ImportUserCertificateService(file.getPath(), false, userPassword);
                        service.execute();
                        tvUserCertificateValid.setVisibility(View.INVISIBLE);
                        tvUserCertificateInvalid.setVisibility(View.VISIBLE);
                        //tvUserCertificateSelfSigned.setVisibility(View.VISIBLE);
                    } else {
                        tvUserCertificateValid.setVisibility(View.VISIBLE);
                        tvUserCertificateInvalid.setVisibility(View.INVISIBLE);
                        //tvUserCertificateSelfSigned.setVisibility(View.INVISIBLE);
                    }
                } catch (SecureSmsException exception) {
                    tvUserCertificateValid.setVisibility(View.INVISIBLE);
                    tvUserCertificateInvalid.setVisibility(View.VISIBLE);
                } finally {
                    etUserCertificate.setText(file.getPath());
                }
                break;
            }
            case PRIVATE_RSA_KEY: {
                try {
                    ImportPrivateKeysService service = new ImportPrivateKeysService(file.getPath(), keysPassword, userPassword);
                    service.execute();
                    tvPrivateRsaKeyValid.setVisibility(View.VISIBLE);
                    tvPrivateRsaKeyInvalid.setVisibility(View.INVISIBLE);
                } catch (SecureSmsException exception) {
                    tvPrivateRsaKeyValid.setVisibility(View.INVISIBLE);
                    tvPrivateRsaKeyInvalid.setVisibility(View.VISIBLE);
                }
                etPrivateRsaKey.setText(file.getPath());
                break;
            }
            case PRIVATE_EC_KEY: {
                try {
                    ImportPrivateKeysService service = new ImportPrivateKeysService(file.getPath(), keysPassword, userPassword);
                    service.execute();
                    tvPrivateEcKeyValid.setVisibility(View.VISIBLE);
                    tvPrivateEcKeyInvalid.setVisibility(View.INVISIBLE);
                } catch (SecureSmsException exception) {
                    tvPrivateEcKeyValid.setVisibility(View.INVISIBLE);
                    tvPrivateEcKeyInvalid.setVisibility(View.VISIBLE);
                }
                etPrivateEcKey.setText(file.getPath());
                break;
            }
        }
    }

    public void showFileDialog(String extension, ImportFileType type) {
        FileDialog openDialog = new OpenFileDialog();
        Bundle args = new Bundle();
        args.putString(FileDialog.EXTENSION, extension);
        args.putInt("type", type.ordinal());
        openDialog.setArguments(args);
        openDialog.setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
        openDialog.show(getSupportFragmentManager(), OpenFileDialog.class.getName());
    }

    //custom listener
    private View.OnClickListener customOnClickListener(final ImportFileType type) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isEnabled())
                showFileDialog(".pem", type);
            }
        };
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
