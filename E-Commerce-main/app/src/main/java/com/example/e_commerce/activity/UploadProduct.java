package com.example.e_commerce.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.e_commerce.Database.MyDatabase;
import com.example.e_commerce.Model.CategoryModel;
import com.example.e_commerce.Model.ProductModel;

import com.example.e_commerce.R;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UploadProduct extends AppCompatActivity {

    ImageView productimage;
    EditText productname, productprice, productquantity,idforupdateordalete,addcateg;
    Spinner proCategory;
    ArrayAdapter adapter;
    Button upload_btn,updateproduct,daleteproduct,Generate,addcategory,deletecategory;
    TextView reset_btn,addCategory;
    MyDatabase database;
    String str1;
    final static int GALLERY_REQUEST_CODE = 101;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);
        getSupportActionBar().setTitle("upload product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intiView();
        addcategory=(Button)findViewById(R.id.addcategory);
        addcateg=(EditText) findViewById(R.id.editcategory);
        deletecategory=(Button) findViewById(R.id.DeleteCategory);

        deletecategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletecategory();
            }
        });
        reset_btn.setOnClickListener(view -> {
            productimage.setImageResource(R.drawable.proimg);
            productname.setText("");
            productprice.setText("");
            productquantity.setText("");
        });

         addcategory.setOnClickListener(view -> {
            String newcategory= addcateg.getText().toString();

             database.insertCategory(new CategoryModel(newcategory),0);
             getAllcategory();
         });
        SharedPreferences preferences=getSharedPreferences("addCategory1",MODE_PRIVATE);
        str1=preferences.getString("add1","show");
        if(str1.equals("hiddin2")){
            addCategory.setText("");
        }

        addCategory.setOnClickListener(view -> {
            if(!str1.equals("hiddin2")){
                addCategory();
            }
            SharedPreferences preferences1 =getSharedPreferences("addCategory1",MODE_PRIVATE);
            SharedPreferences.Editor editor= preferences1.edit();
            editor.putString("add1","hiddin2");
            editor.apply();
            addCategory.setText("");
        });

        getAllcategory();


        productimage.setOnClickListener(v -> chooseImage());

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               uploadProduct();

            }
        });
        updateproduct.setOnClickListener(view -> updateProduct());
        daleteproduct.setOnClickListener(view -> deleteproduct());

        Generate.setOnClickListener(view -> {
        if(idforupdateordalete.getText().toString().replace(" ", "").trim().equals(""))
            Toast.makeText(getApplicationContext(),"Enter id",Toast.LENGTH_SHORT).show();
        else{
            Cursor c= database.getProductbyid(idforupdateordalete.getText().toString());
            productname.setText(c.getString(1));
            productprice.setText(c.getFloat(3)+"");
            productquantity.setText(c.getInt(4)+"");
            InputStream is = new ByteArrayInputStream(c.getBlob(2));
            Bitmap bmp = BitmapFactory.decodeStream(is);
            productimage.setImageBitmap(bmp);//convert byte[] to bit map
        }
        });
    }

    protected void addCategory(){
        database.insertCategory(new CategoryModel("electronics"),0);
        database.insertCategory(new CategoryModel("fashion"),0);
        database.insertCategory(new CategoryModel("cars"),0);
        database.insertCategory(new CategoryModel("sport"),0);
        database.insertCost(0);
    }

    protected void intiView() {
        productimage =(ImageView) findViewById(R.id.product_image);
        productname =(EditText) findViewById(R.id.product_name);
        productprice =(EditText) findViewById(R.id.product_price);
        productquantity =(EditText) findViewById(R.id.product_quantity);
        proCategory =(Spinner) findViewById(R.id.category);
        upload_btn =(Button) findViewById(R.id.btn_upload);
        updateproduct=(Button)findViewById(R.id.UpdateProduct);
        daleteproduct=(Button)findViewById(R.id.DeleteProduct);
        idforupdateordalete=(EditText) findViewById(R.id.id_for_update_del);
        reset_btn =(TextView) findViewById(R.id.reset);
        addCategory=(TextView) findViewById(R.id.addCategory);
        Generate=(Button)findViewById(R.id.generate);
        database =  MyDatabase.getInstance(this);
    }

    protected void chooseImage() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.adminmenu1,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("onOptionsItemSelected", "Item ID: " + item.getItemId());

        switch (item.getItemId()){
            case R.id.report:
                Intent i2 = new Intent(UploadProduct.this, ReportGenerated.class);
                startActivity(i2);
                return true;
            case R.id.chart:
                Intent i3 = new Intent(UploadProduct.this, ChartGenerated.class);
                startActivity(i3);
                return true;
            case R.id.deleteuser:
                 Intent i4=new Intent(UploadProduct.this, deleteUser.class);
                 startActivity(i4);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //onBackPressed();

        return super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                productimage.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    protected static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    protected void getAllcategory(){

        List<String>cate=new ArrayList<>();
        Cursor cursor=database.getCategory();
        if (cursor!=null){
            while (!cursor.isAfterLast()){
                cate.add(cursor.getString(1));
                cursor.moveToNext();
            }
            adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,cate);
            proCategory.setAdapter(adapter);///Spinner
        }
    }

    protected void uploadProduct(){

        String name=productname.getText().toString();
        String price=productprice.getText().toString();
        String quan=productquantity.getText().toString();
        int catid=Integer.parseInt(database.getCatId(proCategory.getSelectedItem().toString()));
        byte [] image=imageViewToByte(productimage);

        if(!name.equals("")||!price.equals("")||!quan.equals(""))
        {
            ProductModel productModel = new ProductModel(getApplicationContext(),Integer.parseInt(quan), catid,name,image,Double.parseDouble(price));
            String sss= database.insertProduct(productModel);

            Toast.makeText(this, sss, Toast.LENGTH_SHORT).show();
            productimage.setImageResource(R.drawable.proimg);
            productname.setText("");
            productprice.setText("");
            productquantity.setText("");


            Toast.makeText(this, "product added", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Check data again", Toast.LENGTH_SHORT).show();
        }
    }

    protected void updateProduct(){

        String name=productname.getText().toString();
        String price=productprice.getText().toString();
        String quan=productquantity.getText().toString();

        int catid=Integer.parseInt(database.getCatId(proCategory.getSelectedItem().toString()));
        byte [] image=imageViewToByte(productimage);

        if(!name.equals("")||!price.equals("")||!quan.equals(""))
        {
            ProductModel productModel = new ProductModel(getApplicationContext(),Integer.parseInt(quan), catid,name,image,Double.parseDouble(price));
            database.updateProduct(productModel,idforupdateordalete.getText().toString());



            Toast.makeText(this, "product updated", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Check data again", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteproduct(){
        String se= database.daleteProduct(idforupdateordalete.getText().toString());
        Toast.makeText(this, se, Toast.LENGTH_SHORT).show();
    }
    
    public void deletecategory(){
        if(idforupdateordalete.getText().toString().equals("")){
            Toast.makeText(context, "enter id to delete", Toast.LENGTH_SHORT).show();
        }
        else{
        String se= database.deleteCategory(idforupdateordalete.getText().toString());
        Toast.makeText(this, se, Toast.LENGTH_SHORT).show();
    }}

}
