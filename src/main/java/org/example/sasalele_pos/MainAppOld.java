package org.example.sasalele_pos;

import org.example.sasalele_pos.database.DBConnection;
import org.example.sasalele_pos.model.*;
import org.example.sasalele_pos.services.AuthService;
import org.example.sasalele_pos.services.ProductService;
import org.example.sasalele_pos.services.TransactionService;
import org.example.sasalele_pos.utils.InputValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainAppOld {
    private static final Scanner scanner = new Scanner(System.in);
    private static AuthService authService;
    private static ProductService productService;
    private static TransactionService transactionService;
    private static User currentUser;

    public static void main(String[] args) {
        initializeSystem();
        showMainMenu();
    }

    private static void initializeSystem() {
        DBConnection.initializeDatabase();
        authService = new AuthService();
        productService = new ProductService();
        transactionService = new TransactionService();
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== SELAMAT DATANG DI SASA LELE POS ===");
            System.out.println("1. Login");
            System.out.println("2. Keluar");
            System.out.print("Pilih menu: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            switch (choice) {
                case 1 -> loginMenu();
                case 2 -> {
                    System.out.println("Terima kasih!");
                    System.exit(0);
                }
                default -> System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void loginMenu() {
        System.out.print("\nUsername: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        boolean isAuthenticated = authService.loginUser(username, password);
        if (isAuthenticated) {
            currentUser = authService.getCurrentUser(); // ✅ Sekarang method ini tersedia
            System.out.println("Login berhasil!");
            showDashboard();
        } else {
            System.out.println("Username/password salah!");
        }
    }

    private static void showDashboard() {
        while (true) {
            System.out.println("\n=== DASHBOARD UTAMA ===");
            System.out.println("1. Transaksi Penjualan");
            System.out.println("2. Manajemen Produk");
            if (currentUser.getRole().equals("admin")) {
                System.out.println("3. Manajemen Pengguna");
            }
            System.out.println("4. Logout");
            System.out.print("Pilih menu: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> transactionMenu();
                case 2 -> productManagementMenu();
                case 3 -> {
                    if (currentUser.getRole().equals("admin")) {
                        userManagementMenu();
                    }
                }
                case 4 -> {
                    currentUser = null;
                    return;
                }
                default -> System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void transactionMenu() {
        List<CartItem> cartItems = new ArrayList<>();

        while (true) {
            System.out.println("\n=== TRANSAKSI PENJUALAN ===");
            System.out.println("1. Tambah Produk");
            System.out.println("2. Lihat Keranjang");
            System.out.println("3. Proses Pembayaran");
            System.out.println("4. Kembali");
            System.out.print("Pilih menu: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addProductToCart(cartItems);
                case 2 -> showCart(cartItems);
                case 3 -> processPayment(cartItems);
                case 4 -> { return; }
                default -> System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void addProductToCart(List<CartItem> cartItems) {
        System.out.print("\nMasukkan ID Produk: ");
        String productId = scanner.nextLine();

        Product product = productService.getProductById(productId).orElse(null);
        if (product == null) {
            System.out.println("Produk tidak ditemukan!");
            return;
        }

        System.out.print("Jumlah: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        if (InputValidator.isValidQuantity(quantity)) {
            cartItems.add(new CartItem(product, quantity));
            System.out.println("Produk berhasil ditambahkan!");
        } else {
            System.out.println("Jumlah tidak valid!");
        }
    }

    private static void showCart(List<CartItem> cartItems) {
        System.out.println("\n=== KERANJANG BELANJA ===");
        if (cartItems.isEmpty()) {
            System.out.println("Keranjang kosong!");
            return;
        }

        double total = 0;
        for (CartItem item : cartItems) {
            System.out.printf("%s x%d - Rp%,.2f%n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.calculateSubtotal()
            );
            total += item.calculateSubtotal();
        }
        System.out.println("Total Belanja: Rp" + String.format("%,.2f", total));
    }

    private static void processPayment(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            System.out.println("Keranjang kosong!");
            return;
        }

        double total = cartItems.stream()
                .mapToDouble(CartItem::calculateSubtotal)
                .sum();

        System.out.printf("\nTotal Belanja: Rp%,.2f%n", total);
        System.out.print("Masukkan Jumlah Pembayaran: ");
        double payment = scanner.nextDouble();
        scanner.nextLine();

        if (InputValidator.isPaymentSufficient(payment, total)) {
            try {
                double change = transactionService.processSale(cartItems, payment, currentUser.getUsername());
                System.out.printf("Kembalian: Rp%,.2f%n", change);
                System.out.println("Transaksi berhasil!");
                cartItems.clear();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Pembayaran tidak cukup!");
        }
    }

    private static void productManagementMenu() {
        if (!currentUser.getRole().equals("admin")) {
            System.out.println("Akses ditolak!");
            return;
        }

        while (true) {
            System.out.println("\n=== MANAJEMEN PRODUK ===");
            System.out.println("1. Tambah Produk");
            System.out.println("2. Lihat Daftar Produk");
            System.out.println("3. Kembali");
            System.out.print("Pilih menu: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addProduct();
                case 2 -> listProducts();
                case 3 -> { return; }
                default -> System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private static void addProduct() {
        System.out.println("\n=== TAMBAH PRODUK ===");
        System.out.print("Jenis Produk (1. Reguler, 2. Perishable): ");
        int type = scanner.nextInt();
        scanner.nextLine();

        System.out.print("ID Produk: ");
        String id = scanner.nextLine();
        System.out.print("Nama Produk: ");
        String name = scanner.nextLine();
        System.out.print("Harga: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        try {
            switch (type) {
                case 1 -> {
                    productService.addProduct(new NonPerishableProduct(id, name, price));
                    System.out.println("Produk reguler berhasil ditambahkan!");
                }
                case 2 -> {
                    System.out.print("Tanggal Kadaluarsa (YYYY-MM-DD): ");
                    LocalDate expiryDate = LocalDate.parse(scanner.nextLine());
                    productService.addProduct(new PerishableProduct(id, name, price, expiryDate));
                    System.out.println("Produk perishable berhasil ditambahkan!");
                }
                default -> System.out.println("Jenis produk tidak valid!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listProducts() {
        System.out.println("\n=== DAFTAR PRODUK ===");
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("Tidak ada produk!");
            return;
        }

        for (Product p : products) {
            System.out.println(p);
        }
    }

    private static void userManagementMenu() {
        System.out.println("\n=== MANAJEMEN PENGGUNA ===");
        // Implementasi tambah/ubah/hapus pengguna bisa ditambahkan di sini
        System.out.println("Fitur dalam pengembangan!");
    }
}