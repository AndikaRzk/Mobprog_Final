package com.example.project_mobrog;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.project_mobrog.adapter.ForecastAdapter;
import com.example.project_mobrog.api.ApiClient;
import com.example.project_mobrog.api.OkHttpHelper;
import com.example.project_mobrog.api.VolleyHelper;
import com.example.project_mobrog.api.WeatherApiService;
import com.example.project_mobrog.model.ForecastResponse;
import com.example.project_mobrog.model.WeatherResponse;
import com.example.project_mobrog.utils.PreferenceManager;
import com.example.project_mobrog.utils.WeatherUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MainActivity - Main screen of Cloudly Weather App
 * 
 * Demonstrates usage of:
 * - Retrofit with Gson for API calls
 * - Volley for simple requests (alternative)
 * - OkHttp for manual requests (alternative)
 * - Picasso for loading weather icons
 * - SwipeRefreshLayout for pull-to-refresh
 * - RecyclerView for forecast list
 * - SharedPreferences for search history
 */
public class MainActivity extends AppCompatActivity {

    // UI Components
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout mainContainer;
    private EditText editTextCity;
    private ImageButton buttonSearch;
    private MaterialCardView currentWeatherCard;
    private TextView textCityName;
    private ImageView imageWeatherIcon;
    private TextView textTemperature;
    private TextView textWeatherDescription;
    private TextView textHumidity;
    private TextView textPressure;
    private TextView textWind;
    private TextView textForecastHeader;
    private RecyclerView recyclerViewForecast;
    private ProgressBar progressBar;
    private LinearLayout emptyState;

    // Adapters & Helpers
    private ForecastAdapter forecastAdapter;
    private PreferenceManager preferenceManager;
    private WeatherApiService apiService;

    // Current data
    private String currentCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.swipeRefreshLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        setupListeners();
        
        // Initialize helpers
        preferenceManager = new PreferenceManager(this);
        apiService = ApiClient.getApiService();
        
        // Load last searched city if available
        String lastCity = preferenceManager.getLastCity();
        if (lastCity != null && !lastCity.isEmpty()) {
            editTextCity.setText(lastCity);
            searchWeather(lastCity);
        }
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mainContainer = findViewById(R.id.mainContainer);
        editTextCity = findViewById(R.id.editTextCity);
        buttonSearch = findViewById(R.id.buttonSearch);
        currentWeatherCard = findViewById(R.id.currentWeatherCard);
        textCityName = findViewById(R.id.textCityName);
        imageWeatherIcon = findViewById(R.id.imageWeatherIcon);
        textTemperature = findViewById(R.id.textTemperature);
        textWeatherDescription = findViewById(R.id.textWeatherDescription);
        textHumidity = findViewById(R.id.textHumidity);
        textPressure = findViewById(R.id.textPressure);
        textWind = findViewById(R.id.textWind);
        textForecastHeader = findViewById(R.id.textForecastHeader);
        recyclerViewForecast = findViewById(R.id.recyclerViewForecast);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupRecyclerView() {
        forecastAdapter = new ForecastAdapter();
        recyclerViewForecast.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewForecast.setAdapter(forecastAdapter);
    }

    private void setupListeners() {
        // Search button click
        buttonSearch.setOnClickListener(v -> {
            String city = editTextCity.getText().toString().trim();
            if (!city.isEmpty()) {
                hideKeyboard();
                searchWeather(city);
            } else {
                Toast.makeText(this, "Masukkan nama kota", Toast.LENGTH_SHORT).show();
            }
        });

        // Keyboard search action
        editTextCity.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String city = editTextCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    hideKeyboard();
                    searchWeather(city);
                }
                return true;
            }
            return false;
        });

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!currentCity.isEmpty()) {
                searchWeather(currentCity);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Main search method - uses Retrofit for API calls
     */
    private void searchWeather(String city) {
        if (!isNetworkAvailable()) {
            showError("Tidak ada koneksi internet");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        currentCity = city;
        showLoading(true);

        // Using Retrofit for main API calls
        fetchCurrentWeatherWithRetrofit(city);
        fetchForecastWithRetrofit(city);
    }

    /**
     * Fetch current weather using Retrofit (recommended approach)
     */
    private void fetchCurrentWeatherWithRetrofit(String city) {
        apiService.getCurrentWeather(city, ApiClient.API_KEY, "metric")
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            displayCurrentWeather(response.body());
                            preferenceManager.saveLastCity(city);
                        } else if (response.code() == 404) {
                            showError("Kota tidak ditemukan");
                            hideWeatherData();
                        } else {
                            showError("Gagal mengambil data cuaca");
                        }
                        showLoading(false);
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        showError("Error: " + t.getMessage());
                        showLoading(false);
                    }
                });
    }

    /**
     * Fetch forecast using Retrofit
     */
    private void fetchForecastWithRetrofit(String city) {
        apiService.getForecast(city, ApiClient.API_KEY, "metric")
                .enqueue(new Callback<ForecastResponse>() {
                    @Override
                    public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            displayForecast(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastResponse> call, Throwable t) {
                        // Silently fail for forecast, main weather is more important
                    }
                });
    }

    /**
     * Alternative: Fetch current weather using Volley
     * Uncomment to use Volley instead of Retrofit
     */
    @SuppressWarnings("unused")
    private void fetchCurrentWeatherWithVolley(String city) {
        VolleyHelper.getInstance(this).getCurrentWeather(
                city,
                ApiClient.API_KEY,
                response -> {
                    displayCurrentWeather(response);
                    preferenceManager.saveLastCity(city);
                    showLoading(false);
                },
                error -> {
                    showError("Volley Error: " + error.getMessage());
                    showLoading(false);
                }
        );
    }

    /**
     * Alternative: Fetch current weather using OkHttp
     * Uncomment to use OkHttp instead of Retrofit
     */
    @SuppressWarnings("unused")
    private void fetchCurrentWeatherWithOkHttp(String city) {
        OkHttpHelper.getInstance().getCurrentWeather(
                city,
                ApiClient.API_KEY,
                new OkHttpHelper.WeatherCallback() {
                    @Override
                    public void onSuccess(WeatherResponse response) {
                        displayCurrentWeather(response);
                        preferenceManager.saveLastCity(city);
                        showLoading(false);
                    }

                    @Override
                    public void onError(String message) {
                        showError("OkHttp Error: " + message);
                        showLoading(false);
                    }
                }
        );
    }

    /**
     * Display current weather data
     */
    private void displayCurrentWeather(WeatherResponse weather) {
        emptyState.setVisibility(View.GONE);
        currentWeatherCard.setVisibility(View.VISIBLE);

        // City name
        String cityCountry = weather.getName();
        if (weather.getSys() != null && weather.getSys().getCountry() != null) {
            cityCountry += ", " + weather.getSys().getCountry();
        }
        textCityName.setText(cityCountry);

        // Temperature
        textTemperature.setText(WeatherUtils.formatTemperature(weather.getMain().getTemp()));

        // Weather details
        textHumidity.setText(WeatherUtils.formatHumidity(weather.getMain().getHumidity()));
        textPressure.setText(WeatherUtils.formatPressure(weather.getMain().getPressure()));
        textWind.setText(WeatherUtils.formatWindSpeed(weather.getWind().getSpeed()));

        // Weather description and icon
        if (weather.getWeather() != null && !weather.getWeather().isEmpty()) {
            WeatherResponse.Weather w = weather.getWeather().get(0);
            
            // Capitalize description
            String description = w.getDescription();
            description = description.substring(0, 1).toUpperCase() + description.substring(1);
            textWeatherDescription.setText(description);

            // Load icon with Picasso
            String iconUrl = ApiClient.getIconUrl(w.getIcon());
            Picasso.get()
                    .load(iconUrl)
                    .into(imageWeatherIcon);

            // Update background based on weather and time
            boolean isNight = false;
            if (weather.getSys() != null) {
                isNight = WeatherUtils.isNightTime(
                        weather.getDt(),
                        weather.getSys().getSunrise(),
                        weather.getSys().getSunset()
                );
            }
            int bgDrawable = WeatherUtils.getBackgroundForWeather(w.getMain(), isNight);
            mainContainer.setBackgroundResource(bgDrawable);
        }
    }

    /**
     * Display 5-day forecast
     */
    private void displayForecast(ForecastResponse forecast) {
        textForecastHeader.setVisibility(View.VISIBLE);
        recyclerViewForecast.setVisibility(View.VISIBLE);

        // Filter to get one forecast per day (noon time)
        List<ForecastResponse.ForecastItem> dailyForecast = new ArrayList<>();
        String lastDate = "";
        
        for (ForecastResponse.ForecastItem item : forecast.getList()) {
            String date = item.getDtTxt().split(" ")[0];
            if (!date.equals(lastDate)) {
                dailyForecast.add(item);
                lastDate = date;
            }
            if (dailyForecast.size() >= 5) break;
        }

        forecastAdapter.setForecastList(dailyForecast);
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void hideWeatherData() {
        currentWeatherCard.setVisibility(View.GONE);
        textForecastHeader.setVisibility(View.GONE);
        recyclerViewForecast.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        Snackbar.make(mainContainer, message, Snackbar.LENGTH_LONG).show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}