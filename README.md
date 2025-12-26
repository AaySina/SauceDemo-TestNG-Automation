# SauceDemo TestNG Automation Framework

Proyek ini adalah kerangka kerja otomatisasi UI komprehensif yang dikembangkan menggunakan **Java**, **Selenium WebDriver**, dan **TestNG**. Framework ini dirancang untuk memvalidasi fungsionalitas Login di aplikasi e-commerce Sauce Demo, memenuhi persyaratan dari dua assignment (Assigment 1: Assertions & DDT, Assigment 2: Listener & Reporting).

## Fitur Utama Framework

Framework ini mengimplementasikan praktik terbaik industri, termasuk:

* **Page Object Model (POM):** Pemisahan kode yang jelas antara logika tes, halaman, dan *core utilities*.
* **Data Driven Testing (DDT):** Menggunakan **Apache POI** dan **TestNG @DataProvider** untuk membaca data login dari file Excel (`login-data-test.xlsx`).
* **Reporting Lanjutan:** Menggunakan **TestNG ITestListener** (`ExtentReportListener`) untuk menghasilkan laporan HTML interaktif dengan **ExtentReports**.
* **Automated Screenshots:** Mampu mengambil dan melampirkan *screenshot* secara otomatis untuk **setiap tes yang PASS maupun FAIL** (berkat *Listener* yang sudah dikembangkan).
* **Validasi Robust:** Menggunakan **Hard Assertions** dan **Soft Assertions** untuk berbagai skenario pengujian.
* **Logging Profesional:** Integrasi **Log4j 2** untuk mencatat setiap langkah eksekusi ke console dan file (`test-logs/automation.log`).

## Skenario Pengujian (Test Scenarios)

Semua tes login berada di `LoginTest.java` dan dijalankan melalui TestNG Suite XML.

| Fitur | Skenario | Assertion Type |
| :--- | :--- | :--- |
| **Login Success** | Login dengan *credential* standar. | Hard Assert (Validasi Navigasi URL). |
| **Login Failure** | Login dengan *user* yang di-lock out. | Hard Assert (Validasi Pesan Error). |
| **Login Edge Case** | Login dengan password kosong. | Hard Assert (Validasi Pesan Error Spesifik). |
| **Problem User** | Login dengan *problem_user*. | Hard Assert (Validasi Navigasi Post-Login). |
| **Data Driven Test** | Menguji beberapa *dataset* (sukses/gagal) dari file Excel. | Soft Assert (Untuk mengumpulkan semua kegagalan). |

## Teknologi yang Digunakan

* **Bahasa:** Java (JDK 21+)
* **Web Automation:** Selenium WebDriver
* **Test Runner & Assertions:** TestNG
* **Reporting:** ExtentReports (dengan Custom Listener)
* **Logging:** Log4j 2
* **Data Handling:** Apache POI (untuk Excel)
* **Dependency Management:** Gradle

## ara Menjalankan Proyek (Execution)

1.  **Clone Repository:**
    ```bash
    git clone [https://github.com/AaySina/SauceDemo-TestNG-Automation.git](https://github.com/AaySina/SauceDemo-TestNG-Automation.git)
    cd SauceDemo-TestNG-Automation
    ```

2.  **Build Proyek:** Pastikan semua *dependency* terunduh.
    ```bash
    ./gradlew build
    ```

3.  **Jalankan Test Suite:** Gunakan *task* Gradle dengan menentukan file *suite* XML yang diinginkan.

    * **Untuk Menjalankan Semua Tes (Smoke Suite):**
        ```bash
        ./gradlew test -Dsuite=smoke.xml
        ```

    * **Untuk Melihat Hasil Laporan:** Buka file `test-reports/ExecutionReport_<timestamp>.html` di browser.
