package com.project.bbt.Item;

import static com.project.bbt.menu.txt_alpha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

//import com.itextpdf.text.Document;
//import com.itextpdf.text.FontFactory;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
import com.project.bbt.MainActivity;
import com.project.bbt.NukeSSLCerts;
import com.project.bbt.R;
import com.project.bbt.kalendar_event;
import com.project.bbt.ketentuan;
import com.project.bbt.menu;
import com.project.bbt.model.PaymentModel;
import com.project.bbt.setting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class menu_payment extends AppCompatActivity {

    ImageButton slipgaji, refund;
    DrawerLayout dLayout;
    SharedPreferences sharedPreferences;

    String string_nip_karyawan, string_lokasi_karyawan, string_jabatan_karyawan,
            string_nama_karyawan, string_nama_divisi, string_id_jabatan, string_no_urut_karyawan;

    String selectedBulan, selectedTahun;

    private ListView listView;
    private ArrayList<PaymentModel> paymentList;
    private PaymentAdapter paymentAdapter;
    RelativeLayout relative_payment_bg_white, relative_data_tidak_ditemukan;
    Button btnGeneratePdf;
    ImageButton btn_calendar_event;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_payment);

        NukeSSLCerts.nuke();

        // Initialize UI components
        listView = findViewById(R.id.listView);
        paymentList = new ArrayList<>();
        paymentAdapter = new PaymentAdapter(this, paymentList);
        listView.setAdapter(paymentAdapter);
        relative_payment_bg_white = findViewById(R.id.relative_payment_bg_white);
        btn_calendar_event = findViewById(R.id.btn_calendar_event);
        relative_data_tidak_ditemukan = findViewById(R.id.relative_data_tidak_ditemukan);

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        string_nip_karyawan = sharedPreferences.getString("str_nip_karyawan", null);
        string_lokasi_karyawan = sharedPreferences.getString("str_lokasi_karyawan", null);
        string_jabatan_karyawan = sharedPreferences.getString("str_jabatan_karyawan", null);
        string_nama_karyawan = sharedPreferences.getString("str_nama_karyawan", null);
        string_nama_divisi = sharedPreferences.getString("str_nama_divisi", null);
        string_id_jabatan = sharedPreferences.getString("str_id_jabatan", null);
        string_no_urut_karyawan = sharedPreferences.getString("str_nourut_karyawan", null);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.material_toolbar); // get the reference of Toolbar
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar
        setTitle("Payment");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dLayout.openDrawer(Gravity.LEFT);
            }
        });

        setNavigationDrawer();

        Calendar c1 = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        String strdate1 = sdf1.format(c1.getTime());

        showMonthYearDialog();

        btnGeneratePdf = findViewById(R.id.btnGeneratePdf);

        btnGeneratePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermission();
            }
        });

        btn_calendar_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMonthYearDialog();
            }
        });

//        checkPermissions();

    }

    private void setNavigationDrawer() {
        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // initiate a DrawerLayout
        NavigationView navView = (NavigationView) findViewById(R.id.navigation); // initiate a Navigation View
        View headerView = navView.getHeaderView(0);
        final TextView hari = headerView.findViewById(R.id.hari);
        final TextView kondisi = headerView.findViewById(R.id.kondisi);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                hari.setText(currentDateandTime);
                handler.postDelayed(this, 1000);

                Calendar c = Calendar.getInstance();
                int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

                if (timeOfDay >= 0 && timeOfDay < 9) {
                    kondisi.setText("Selamat Pagi");
                } else if (timeOfDay >= 9 && timeOfDay < 15) {
                    kondisi.setText("Selamat Siang");
                } else if (timeOfDay >= 15 && timeOfDay < 18) {
                    kondisi.setText("Selamat Sore");
                } else {
                    kondisi.setText("Selamat Malam");
                }
            }
        };
        runnable.run();
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment frag = null; // create a Fragment Object
                int itemId = menuItem.getItemId(); // get selected menu item's id
                if (itemId == R.id.nav_home) {
                    Intent i = new Intent(getApplicationContext(), menu.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                } else if (itemId == R.id.password) {
                    Intent i = new Intent(getApplicationContext(), setting.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                } else if (itemId == R.id.ketentuan) {
                    Intent i = new Intent(getApplicationContext(), ketentuan.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                } else if (itemId == R.id.nav_exit) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            menu_payment.this);
                    alertDialogBuilder.setTitle("Anda yakin untuk Logout ?");
                    alertDialogBuilder
                            .setMessage("Klik Ya untuk keluar!")
                            .setCancelable(false)
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();
                                    Intent intent = new Intent(menu_payment.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else if (itemId == R.id.nav_calendar) {
                    Intent i = new Intent(getApplicationContext(), kalendar_event.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                }
                if (frag != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.commit(); // commit the changes
                    dLayout.closeDrawers(); // close the all open Drawer Views
                    return true;
                }
                return false;
            }
        });
    }

    private void showMonthYearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_select_month_year, null);
        Spinner spinnerBulan = view.findViewById(R.id.spinner_bulan);
        Spinner spinnerTahun = view.findViewById(R.id.spinner_tahun);
        Button btnOk = view.findViewById(R.id.btn_ok);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        // Set up Spinner Tahun dengan nilai dari tahun sekarang hingga mundur 5 tahun
        ArrayList<String> tahunList = new ArrayList<>();
        int tahunSekarang = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i <= 5; i++) {
            tahunList.add(String.valueOf(tahunSekarang - i));
        }

        int bulanSekarang = Calendar.getInstance().get(Calendar.MONTH);
        spinnerBulan.setSelection(bulanSekarang);

        ArrayAdapter<String> adapterTahun = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tahunList);
        adapterTahun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTahun.setAdapter(adapterTahun);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background); // Set background dialog
        dialog.show();

        btnOk.setOnClickListener(v -> {
            selectedBulan = String.valueOf(spinnerBulan.getSelectedItemPosition() + 1); // Mengambil bulan dalam angka (1-12)
            selectedTahun = spinnerTahun.getSelectedItem().toString();

            dialog.dismiss();
//            fetchDataFromAPI(selectedBulan, selectedTahun);

            fetchPaymentData(string_no_urut_karyawan, selectedBulan, selectedTahun);
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            // Kembali ke menu utama jika dibatalkan
            Intent intent = new Intent(menu_payment.this, menu.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchPaymentData(String string_no_urut_karyawan, String selectedBulan, String selectedTahun) {
        String url = "https://ess.banktanah.id/bbt_api/rest_server/api/absensi/index_get_payment?bulan_start=" + selectedBulan + "&bulan_end=" + selectedBulan + "&tahun=" + selectedTahun + "&noUrut=" + string_no_urut_karyawan;  // Ganti dengan URL API

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {

                    relative_payment_bg_white.setVisibility(View.GONE);
                    btnGeneratePdf.setVisibility(View.GONE);
                    relative_data_tidak_ditemukan.setVisibility(View.GONE);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean status = jsonObject.getBoolean("status");

                        if (status) {
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            paymentList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                PaymentModel payment = new PaymentModel(
                                        item.getString("noUrut"),
                                        item.getString("namaKaryawan"),
                                        item.getString("lokasiHrd"),
                                        item.getString("jabatan"),
                                        item.getString("divisi"),
                                        item.getString("bagian"),
                                        item.getString("jenisKelamin"),
                                        item.getString("tanggalJoin"),
                                        item.getString("npwp"),
                                        item.getString("golongan"),
                                        item.getString("noRek"),
                                        item.getString("namaRek"),
                                        item.getString("digit_ktp"),
                                        item.getString("digit_npwp"),
                                        item.getString("digit_bpjs_ket"),
                                        item.getString("digit_bpjs_kes"),
                                        item.getString("gajiPokok"),
                                        item.getString("allowanceProject"),
                                        item.getString("tunjFungsional"),
                                        item.getString("tunjJabatan"),
                                        item.getString("tunjKinerja"),
                                        item.getString("TunjKomunikasi"),
                                        item.getString("tunjTransportasiAllowance"),
                                        item.getString("bonus"),
                                        item.getString("asuransiKesAllowance"),
                                        item.getString("perjalananDinasAllowance"),
                                        item.getString("jkkJkmJpkAllowance"),
                                        item.getString("jhtcAllowance"),
                                        item.getString("bpjsByCompanyAllowance"),
                                        item.getString("jpByCompanyAllowance"),
                                        item.getString("jhteAllowance"),
                                        item.getString("jpByEmployeeAllowance"),
                                        item.getString("bpjsByEmployeeAllowance"),
                                        item.getString("telatDeduction"),
                                        item.getString("absenDeduction"),
                                        item.getString("tunjTransportasiDeduction"),
                                        item.getString("asuransiKesDeduction"),
                                        item.getString("perjalananDinasDeduction"),
                                        item.getString("jkkJkmJpkDeduction"),
                                        item.getString("jhtcDeduction"),
                                        item.getString("bpjsByCompanyDeduction"),
                                        item.getString("jpByCompanyDeduction"),
                                        item.getString("jhteDeduction"),
                                        item.getString("jpByEmployeeDeduction"),
                                        item.getString("bpjsByEmployeeDeduction"),
                                        item.getString("thp"),
                                        item.getString("bruto"),
                                        item.getString("tax"),
                                        item.getString("bulan"),
                                        item.getString("tahun")
                                );
                                paymentList.add(payment);
                            }

                            paymentAdapter.notifyDataSetChanged();

                        } else {
                            relative_payment_bg_white.setVisibility(View.GONE);
                            btnGeneratePdf.setVisibility(View.GONE);
                            relative_data_tidak_ditemukan.setVisibility(View.VISIBLE);
                            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },

                error -> {
                    relative_data_tidak_ditemukan.setVisibility(View.VISIBLE);
                    btnGeneratePdf.setVisibility(View.GONE);
                    relative_payment_bg_white.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", "admin", "Dev24");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public class PaymentAdapter extends ArrayAdapter<PaymentModel> {
        private Context context;
        private List<PaymentModel> paymentList;

        public PaymentAdapter(Context context, List<PaymentModel> paymentList) {
            super(context, 0, paymentList);
            this.context = context;
            this.paymentList = paymentList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_payment, parent, false);
            }

            PaymentModel payment = paymentList.get(position);

            TextView tvNamaKaryawan = convertView.findViewById(R.id.tvNamaKaryawan);
            TextView tvGajiPokok = convertView.findViewById(R.id.tvGajiPokok);
            TextView tvThp = convertView.findViewById(R.id.tvThp);
            TextView tvJabatan = convertView.findViewById(R.id.tvJabatan);
            TextView tvDivisi = convertView.findViewById(R.id.tvDivisi);
            TextView tvBagian = convertView.findViewById(R.id.tvBagian);

            TextView tvNpwp = convertView.findViewById(R.id.tvNpwp);
            TextView tvGolongan = convertView.findViewById(R.id.tvGolongan);
            TextView tvNoRek = convertView.findViewById(R.id.tvNoRek);
            TextView tvNamaRek = convertView.findViewById(R.id.tvNamaRek);
            TextView tvAllowanceProject = convertView.findViewById(R.id.tvAllowanceProject);
            TextView tvTunjFungsional = convertView.findViewById(R.id.tvTunjFungsional);
            TextView tvTunjJabatan = convertView.findViewById(R.id.tvTunjJabatan);
            TextView tvTunjKinerja = convertView.findViewById(R.id.tvTunjKinerja);
            TextView tvTunjKomunikasi = convertView.findViewById(R.id.tvTunjKomunikasi);

            TextView tvTunjTransportasiAllowance = convertView.findViewById(R.id.tvTunjTransportasiAllowance);
            TextView tvBonus = convertView.findViewById(R.id.tvBonus);
            TextView tvTelatDeduction = convertView.findViewById(R.id.tvTelatDeduction);
            TextView tvAbsenDeduction = convertView.findViewById(R.id.tvAbsenDeduction);
            TextView tvBruto = convertView.findViewById(R.id.tvBruto);
            TextView tvTax = convertView.findViewById(R.id.tvTax);

            TextView tvBulan = convertView.findViewById(R.id.tv_month);
            TextView tvTahun = convertView.findViewById(R.id.tv_year);

            // Array nama bulan
            String[] namaBulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                    "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

            try {
                // Ambil nomor bulan dari payment.getBulan()
                int bulanIndex = Integer.parseInt(payment.getBulan()) - 1;

                // Pastikan index dalam rentang yang valid (0-11)
                if (bulanIndex >= 0 && bulanIndex < 12) {
                    tvBulan.setText(namaBulan[bulanIndex]);
                } else {
                    tvBulan.setText("-"); // Jika bulan tidak valid
                }
            } catch (NumberFormatException e) {
                tvBulan.setText("-"); // Jika parsing gagal
            }

            tvTahun.setText(payment.getTahun());

//            TextView tvNoUrut = convertView.findViewById(R.id.tvNoUrut);
//            TextView tvLokasiHrd = convertView.findViewById(R.id.tvLokasiHrd);
//            TextView tvBagian = convertView.findViewById(R.id.tvBagian);
//            TextView tvJenisKelamin = convertView.findViewById(R.id.tvJenisKelamin);
//            TextView tvTanggalJoin = convertView.findViewById(R.id.tvTanggalJoin);
//            TextView tvDigitKtp = convertView.findViewById(R.id.tvDigitKtp);
//            TextView tvDigitNpwp = convertView.findViewById(R.id.tvDigitNpwp);
//            TextView tvDigitBpjsKet = convertView.findViewById(R.id.tvDigitBpjsKet);
//            TextView tvDigitBpjsKes = convertView.findViewById(R.id.tvDigitBpjsKes);


            // Set Nama Pegawai
//            tvNamaKaryawan.setText(payment.getNamaKaryawan());
//
//            // Set Gaji Pokok dengan simbol Rupiah
//            String formattedGajiPokok = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getGajiPokok()));
//            tvGajiPokok.setText(formattedGajiPokok);
//
//            // Set THP dengan simbol Rupiah
//            String formattedThp = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getThp()));
//            tvThp.setText(formattedThp);
//
//            // Set Jabatan dan Divisi jika ada
//            tvJabatan.setText(payment.getJabatan());
//            tvDivisi.setText(payment.getDivisi());

            // Set Nama Pegawai
            tvNamaKaryawan.setText(payment.getNamaKaryawan());
            tvJabatan.setText(payment.getJabatan());
            tvDivisi.setText(payment.getDivisi());
            tvBagian.setText(payment.getBagian());

            tvNpwp.setText(payment.getNpwp());
            tvGolongan.setText(payment.getGolongan());

            tvNoRek.setText((payment.getNoRek() != "null" && !payment.getNoRek().trim().isEmpty()) ?
                    payment.getNoRek() : "-");

            tvNamaRek.setText((payment.getNamaRek() != "null" && !payment.getNamaRek().trim().isEmpty()) ?
                    payment.getNamaRek() : "-");

//            tvNoUrut.setText(payment.getNoUrut());
//            tvLokasiHrd.setText(payment.getLokasiHrd());
//            tvBagian.setText(payment.getBagian());
//            tvJenisKelamin.setText(payment.getJenisKelamin());
//            tvTanggalJoin.setText(payment.getTanggalJoin());
//            tvDigitKtp.setText(payment.getDigitKtp());
//            tvDigitNpwp.setText(payment.getDigitNpwp());
//            tvDigitBpjsKet.setText(payment.getDigitBpjsKet());
//            tvDigitBpjsKes.setText(payment.getDigitBpjsKes());

            // Set Gaji Pokok dengan simbol Rupiah
            String formattedGajiPokok = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getGajiPokok()));
            tvGajiPokok.setText(formattedGajiPokok);

            // Set Allowance Project dengan simbol Rupiah
            String formattedAllowanceProject = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getAllowanceProject()));
            tvAllowanceProject.setText(formattedAllowanceProject);

            // Set Tunjangan Fungsional dengan simbol Rupiah
            String formattedTunjFungsional = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getTunjFungsional()));
            tvTunjFungsional.setText(formattedTunjFungsional);

            // Set Tunjangan Jabatan dengan simbol Rupiah
            String formattedTunjJabatan = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getTunjJabatan()));
            tvTunjJabatan.setText(formattedTunjJabatan);

            // Set Tunjangan Kinerja dengan simbol Rupiah
            String formattedTunjKinerja = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getTunjKinerja()));
            tvTunjKinerja.setText(formattedTunjKinerja);

            // Set Tunjangan Komunikasi dengan simbol Rupiah
            String formattedTunjKomunikasi = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getTunjKomunikasi()));
            tvTunjKomunikasi.setText(formattedTunjKomunikasi);

            // Set Tunjangan Transportasi Allowance dengan simbol Rupiah
            String formattedTunjTransportasiAllowance = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getTunjTransportasiAllowance()));
            tvTunjTransportasiAllowance.setText(formattedTunjTransportasiAllowance);

            // Set Bonus dengan simbol Rupiah
            String formattedBonus = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getBonus()));
            tvBonus.setText(formattedBonus);

            // Set Deduction Telat dengan simbol Rupiah
            String formattedTelatDeduction = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getTelatDeduction()));
            tvTelatDeduction.setText(formattedTelatDeduction);

            // Set Deduction Absen dengan simbol Rupiah
            String formattedAbsenDeduction = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getAbsenDeduction()));
            tvAbsenDeduction.setText(formattedAbsenDeduction);

            // Set THP dengan simbol Rupiah
            String formattedThp = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getThp()));
            tvThp.setText(formattedThp);

            // Set Bruto dengan simbol Rupiah
            String formattedBruto = "Rp. " + String.format("%,.0f", Double.parseDouble(payment.getBruto()));
            tvBruto.setText(formattedBruto);

            String tax = payment.getTax();

            // Periksa jika tax adalah "null" atau kosong
            if (tax == null || tax.equals("null") || tax.isEmpty()) {
                tvTax.setText("-");
            } else {
                // Set Tax dengan simbol Rupiah
                try {
                    String formattedTax = "Rp. " + String.format("%,.0f", Double.parseDouble(tax));
                    tvTax.setText(formattedTax);
                } catch (NumberFormatException e) {
                    // Tangani jika parsing gagal
                    tvTax.setText("-");
                }
            }


            return convertView;
        }
    }


//    private void generatePdf() {
//        Document document = new Document();
//        String filePath = getExternalFilesDir(null) + "/PaymentData.pdf";
//
//        try {
//            PdfWriter.getInstance(document, new FileOutputStream(filePath));
//            document.open();
//
//            // Menambahkan Judul
//            document.add(new Paragraph("Data Payment", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
//            document.add(new Paragraph(" ")); // Tambahkan jarak
//
//            // Membuat tabel untuk data
//            for (PaymentModel payment : paymentList) {
//                PdfPTable table = new PdfPTable(2);
//                table.setWidthPercentage(100);
//                table.setSpacingBefore(10f);
//                table.setSpacingAfter(10f);
//                table.getDefaultCell().setBorder(0);
//
//                // Menambahkan data ke dalam tabel
//                table.addCell(new Phrase("Nama Karyawan:", FontFactory.getFont(FontFactory.HELVETICA, 12)));
//                table.addCell(new Phrase(payment.getNamaKaryawan(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
//
//                table.addCell(new Phrase("Jabatan:", FontFactory.getFont(FontFactory.HELVETICA, 12)));
//                table.addCell(new Phrase(payment.getJabatan(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
//
//                table.addCell(new Phrase("Divisi:", FontFactory.getFont(FontFactory.HELVETICA, 12)));
//                table.addCell(new Phrase(payment.getDivisi(), FontFactory.getFont(FontFactory.HELVETICA, 12)));
//
//                // Tambahkan tabel pertama ke dokumen
//                document.add(table);
//
//                // Menambahkan jarak sebelum bagian Gaji Pokok
//                document.add(new Paragraph(" ")); // Tambahkan baris kosong sebagai jarak
//                document.add(new Paragraph(" ")); // Tambahkan baris kosong kedua jika ingin lebih besar jaraknya
//
//                //Membuat tabel baru untuk data berikutnya
//                PdfPTable table2 = new PdfPTable(2);
//                table2.setWidthPercentage(100);
//                table2.getDefaultCell().setBorder(0);
//
//                table2.addCell(new Phrase("Gaji Pokok:", FontFactory.getFont(FontFactory.HELVETICA, 12)));
//                table2.addCell(new Phrase("Rp " + formatCurrency(payment.getGajiPokok()), FontFactory.getFont(FontFactory.HELVETICA, 12)));
//
//                table2.addCell(new Phrase("THP:", FontFactory.getFont(FontFactory.HELVETICA, 12)));
//                table2.addCell(new Phrase("Rp " + formatCurrency(payment.getThp()), FontFactory.getFont(FontFactory.HELVETICA, 12)));
//
//                table2.addCell(new Phrase("Tax:", FontFactory.getFont(FontFactory.HELVETICA, 12)));
//                table2.addCell(new Phrase("Rp " + formatCurrency(payment.getTax()), FontFactory.getFont(FontFactory.HELVETICA, 12)));
//
//                // Tambahkan tabel kedua ke dokumen
//                document.add(table2);
//
//                document.close(); // Menutup dokumen
//
//                // Menampilkan dialog setelah PDF berhasil dibuat
//                showPdfDialog(filePath);
//            }
//
//            document.close(); // Menutup dokumen
//            Toast.makeText(this, "PDF Created Successfully at " + filePath, Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to create PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            Log.e("PDFCreationError", "Failed to create PDF: " + e.getMessage(), e);
//        }
//    }

    // Method untuk format currency menjadi lebih rapi
    private String formatCurrency(String value) {
        if (value == null || value.isEmpty()) {
            return "-";
        }
        try {
            return String.format("%,.0f", Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return "-";
        }
    }


    private void showPdfDialog(String filePath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PDF Berhasil Dibuat");
        builder.setMessage("Apakah Anda ingin membuka file PDF?");
        builder.setPositiveButton("Buka", (dialog, which) -> {
            openPdf(filePath);
        });
        builder.setNegativeButton("Tidak", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void openPdf(String filePath) {
        File file = new File(filePath);
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Tidak ada aplikasi untuk membuka PDF", Toast.LENGTH_SHORT).show();
        }
    }



//    private void checkPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//        }
//    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            // Izin sudah diberikan, panggil fungsi generatePdf()
//            generatePdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, panggil fungsi generatePdf()
//                generatePdf();
            } else {
                Toast.makeText(this, "Izin penyimpanan ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        int beta = Integer.parseInt(txt_alpha.getText().toString());


        super.onDestroy();
    }
}