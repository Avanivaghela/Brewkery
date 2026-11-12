# ☕ Brewkery Artisans - Native Android App

A modern, native Android Coffee & Bakery mobile application powered by REST APIs. Built with Kotlin, MVVM architecture, Retrofit, Coroutines, StateFlow, and Material Design 3.

---

## 📱 App Flow & Key Features

### 1. 📜 Menu Catalog & Store Config
- **Live Store Metadata Integration**: Fetches delivery fee ($2.50), tax rate (8.0%), and estimated wait time (20–30 mins) dynamically from the API.
- **Active Order Banner**: Shows real-time order status banner (`#BK-51506`, "Preparing") at the top of the menu when an order is active with a direct **Track** navigation button.
- **Instant Search**: Real-time search filtering across item names, taglines, descriptions, and ingredients.
- **Category Chips**: Filter items by *All Items*, *Hot Coffee*, *Cold Brews*, and *Artisan Bakery*.
- **Product Cards**: High-res images loaded via Coil, gold badge overlays (*BESTSELLER*, *STAFF PICK*, etc.), rating stars, prices, and `+ Customize` action buttons.

### 2. 🎛️ Item Customizer / Detail View
- **Hero Image & Metadata**: Large product visual, badge overlay, title, base price, and description.
- **Key Ingredients**: Custom chip group tags showing item ingredients.
- **Size Selection**: Radio-style selection (*Tall*, *Grande*, *Venti*) updating extra pricing dynamically.
- **Milk Options / Spreads**: Multiple choices (*Oat Milk*, *Almond Milk*, *Whole Milk*).
- **Sugar Levels**: Sweetness level controls (*0% Unsweetened*, *50% Mild*, *100% Standard Sweet*).
- **Quantity Stepper & Dynamic Add to Cart**: Stepper controls (`- 1 +`) dynamically updating total price before adding to cart.

### 3. 🛒 Shopping Cart & Checkout
- **Cart Management**: Add items with custom size, milk, and sugar selections; adjust quantities or remove items.
- **Empty Cart State**: Elegant empty state view with clear cart action.
- **Bill Breakdown Card**:
  - Subtotal
  - Delivery Fee ($2.50)
  - Estimated Tax (8.0%)
  - **Total Payable** calculation
- **Place Order**: `🔒 Place Order Now` action creating a ticket and updating active order status.

### 4. 🏷️ Order Status & Tracking
- **Order Ticket Summary**: Displays Order Ticket ID (`#BK-51506`), status badge (*PREPARING*), estimated wait time, items ordered count, and barista status.
- **Back to Menu**: Returns to catalog with top active order banner visible for tracking.

---

## 🛠️ Architecture & Tech Stack

- **Language**: Kotlin 2.2+
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **UI Framework**: Android View System (XML layouts), ViewBinding, Material Components 3
- **Async & Reactive Data Flow**: Kotlin Coroutines & `StateFlow`
- **Networking**: Retrofit 2 + Gson Converter
- **Image Loading**: Coil 2.7.0
- **Build System**: Gradle with Kotlin DSL (`build.gradle.kts`)

---

## 🌐 API Endpoints Used

1. **Full Menu & Store Config**:
   - `GET` `https://raw.githubusercontent.com/VivekShah138/Brewkery/main/data.json`
2. **Single Item Detail**:
   - `GET` `https://raw.githubusercontent.com/VivekShah138/Brewkery/main/api/items/{id}.json`

---

## 🤖 AI Usage Section (AI Assistant Integration)

During the development of this project, an **AI Agent (Android Studio AI Assistant)** was utilized as an interactive development partner.

### 💬 Prompts Used
1. *"Create clean Kotlin data models for the Brewkery REST API response (data.json and item detail endpoint) including StoreMeta, Category, MenuItem, and Customizations with Gson annotations."*
2. *"Design pixel-perfect XML layouts matching the prototype screens: Menu Catalog with category chips and search bar, Item Customizer with key ingredients and radio-style selections, Cart Checkout with tax/fee summary card, and Order Status screen with ticket badge."*
3. *"Implement dynamic price calculation logic in MainViewModel for customized coffee items considering base price, size extra price, milk option extra price, and quantity stepper."*
4. *"Setup single activity fragment navigation for Menu, Item Detail, Cart, and Order Status with backstack management and live Active Order banner tracking."*

### 🐛 Bug Caught & Fixed with AI
- **Bug Caught**: During initial Gradle compilation, an **AAR Metadata Dependency Mismatch** error occurred because `androidx.core:core-ktx:1.19.1` required `compileSdk 37`, while the module was compiled against `compileSdk 36.1` (`CheckAarMetadataWorkAction` failure).
- **Fix Applied**: AI identified the dependency incompatibility and updated `gradle/libs.versions.toml` to lock `coreKtx` to `1.13.1` and `compileSdk = 36` in `app/build.gradle.kts`. This resolved the build error instantly, allowing full ViewBinding generation and successful compilation.

---

## 🚀 How to Build & Run

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Brewkery.git
   cd Brewkery
   ```
2. **Open in Android Studio**:
   - Open Android Studio -> Select `Open` -> Choose the `Brewkery` folder.
   - Let Gradle sync completely.
3. **Run the App**:
   - Select the `app` run configuration and target an Android Emulator or connected device.
   - Press `Shift + F10` (or click ▶ Run).

### 📦 Debug APK Location
The pre-compiled debug APK is generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```
