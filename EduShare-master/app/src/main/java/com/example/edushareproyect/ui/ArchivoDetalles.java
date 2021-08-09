package com.example.edushareproyect.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.DocumentsContract;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.edushareproyect.R;
import com.example.edushareproyect.RestApiMehotds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArchivoDetalles#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArchivoDetalles extends Fragment {

    private static final int CREATE_FILE = 1;

    TextView txtDetFilename;
    TextView txtDetFileSize;
    TextView txtDetFileDate;
    TextView txtDetFileMail;
    ImageView FileIMG;
    ImageButton btnDownload;

    String Data;
    String FileName;
    String FileExtension;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARCHIVOID = "";
    

    // TODO: Rename and change types of parameters
    private String mArchivoID;
    

    public ArchivoDetalles() {
        // Required empty public constructor
    }

    public ArchivoDetalles(String id){
        this.mArchivoID = id;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param archivoID Parameter 1. 
     * @return A new instance of fragment ArchivoDetalles.
     */
    // TODO: Rename and change types and number of parameters
    public static ArchivoDetalles newInstance(String archivoID) {
        ArchivoDetalles fragment = new ArchivoDetalles();
        Bundle args = new Bundle();
        args.putString(ARCHIVOID, archivoID);
        
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArchivoID = getArguments().getString(ARCHIVOID);
            
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_archivo_detalles, container, false);

        txtDetFilename = root.findViewById(R.id.txtDetFilename);
        txtDetFileSize = root.findViewById(R.id.txtDetFileSize);
        txtDetFileDate = root.findViewById(R.id.txtDetFileDate);
        txtDetFileMail = root.findViewById(R.id.txtDetFileMail);
        FileIMG = root.findViewById(R.id.FileIMG);

        btnDownload = root.findViewById(R.id.btnFileDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    File f = CreateFile(Data,FileName,FileExtension);
                }catch (IOException ie){
                    mostrarDialogo("Error","No se puede descargar el archivo");
                    ie.printStackTrace();
                }

            }
        });

        Bitmap excel = BitmapFactory.decodeResource(getResources(), R.drawable.excel);
        Bitmap word = BitmapFactory.decodeResource(getResources(), R.drawable.word);
        Bitmap pp = BitmapFactory.decodeResource(getResources(), R.drawable.powerpoint);
        Bitmap pdf = BitmapFactory.decodeResource(getResources(), R.drawable.pdf);
        Bitmap default_file = BitmapFactory.decodeResource(getResources(), R.drawable.default_file);

        getDetalles(root.getContext());


        return root;
    }

    //-----------------------------------------------------------------------------------------------------------------------//
    private void getDetalles(Context context){
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject req = new JSONObject();
        try{
            req.put("archivoID",mArchivoID);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, RestApiMehotds.ApiPOSTFileDetail, req, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try{
                        JSONObject data = response.getJSONObject("response");
                        String extension = data.getString("EXTENSION");
                        Bitmap excel = BitmapFactory.decodeResource(getResources(), R.drawable.excel);
                        Bitmap word = BitmapFactory.decodeResource(getResources(), R.drawable.word);
                        Bitmap pp = BitmapFactory.decodeResource(getResources(), R.drawable.powerpoint);
                        Bitmap pdf = BitmapFactory.decodeResource(getResources(), R.drawable.pdf);
                        Bitmap default_file = BitmapFactory.decodeResource(getResources(), R.drawable.default_file);

                        Bitmap img ;
                        img = BitmapFactory.decodeResource(getResources(), R.drawable.default_file);
                        String tipo = "";
                        switch (extension){
                            case "application/pdf":
                                img = pdf;
                                tipo = "Archivo de lectura PDF";
                                break;
                            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                                img = word;
                                tipo = "Documento de Word";
                                break;
                            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                                img = excel;
                                tipo = "Hojas de calculo Excel";
                                break;
                            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                                img = pp;
                                tipo = "Presentacion de PowerPoint";
                                break;
                            default:
                                img = default_file;
                                tipo = data.getString("EXTENSION");
                                break;
                        }

                        txtDetFilename.setText(data.getString("NOMBRE"));
                        txtDetFileSize.setText(tipo);
                        txtDetFileDate.setText("Fecha: "+data.getString("FECHA_CREACION"));
                        txtDetFileMail.setText("De: "+data.getString("CORREO"));

                        FileIMG.setImageBitmap(img);

                        Data = data.getString("DATA");
                        FileName = data.getString("NOMBRE");
                        FileExtension = data.getString("EXTENSION");


                    }catch(Exception e){
                        mostrarDialogo("Error", e.getMessage());
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mostrarDialogo("Error",error.getMessage());

                }
            });

            queue.add(objectRequest);
        }catch(JSONException ex){
            mostrarDialogo("Error",ex.getMessage());
            ex.printStackTrace();
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------------------------------------------//
    private File CreateFile(String data, String name, String extension) throws IOException {


        CreateFolder();
        byte[] dataEncode = android.util.Base64.decode(data, Base64.DEFAULT);

        File archivo =  new File("storage/emulated/0/EduShare", name);
        FileOutputStream fileOutputStream;
        try{
            fileOutputStream = new FileOutputStream(archivo);
            fileOutputStream.write(dataEncode);
            if(archivo!=null){
                fileOutputStream.close();
                Log.d("PATH", archivo.getAbsolutePath());
            }
        }catch (FileNotFoundException fe){
            mostrarDialogo("Error",fe.getMessage());
            fe.printStackTrace();
            return null;
        }

        return archivo;
    }
    //-----------------------------------------------------------------------------------------------------------------------//

    //-----------------------------------------------------------------------------------------------------------------------//
    private void CreateFolder(){
        File folder = new File("storage/emulated/0", "EduShare/");
        boolean success = true;
        boolean existente = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
            existente = false;
        }
        if (success && !existente) {
            mostrarDialogo("Info","Se creo una carpeta de EduShare para guardar los archivos");
        } else {
            mostrarDialogo("Error","No se pudo crear el directorio");
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------//


    //-----------------------------------------------------------------------------------------------------------------------//
    private void mostrarDialogo(String title, String mensaje) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(mensaje)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
    //-----------------------------------------------------------------------------------------------------------------------//





}