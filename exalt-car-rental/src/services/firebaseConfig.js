// Modular Firebase v9+ SDK Imports
import { initializeApp } from "firebase/app";
import { 
  getAuth, 
  signInWithEmailAndPassword, 
  createUserWithEmailAndPassword,
  GoogleAuthProvider, 
  signInWithPopup,     
  signOut 
} from "firebase/auth";
import {
  getFirestore,
  collection,
  onSnapshot,
  getDocs,
  addDoc,
  updateDoc,
  deleteDoc,
  doc,
  query,    
  where     
} from "firebase/firestore";

// 1. Firebase configuration mapped securely from Vite environment variables
const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
  appId: import.meta.env.VITE_FIREBASE_APP_ID,
};

// 2. Initialize Core Services
const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const db = getFirestore(app);

// 3. Global Collection References
export const carsCollectionRef = collection(db, "cars");
export const rentalsCollectionRef = collection(db, "rentals");

/* ==========================================================================
   AUTHENTICATION WRAPPERS
   ========================================================================== */

/**
 * Authenticate existing users via email/password
 */
export const loginUser = async (email, password) => {
  try {
    const userCredential = await signInWithEmailAndPassword(auth, email, password);
    return userCredential.user;
  } catch (error) {
    console.error("Error signing in user:", error.message);
    throw error;
  }
};

/**
 * Register completely new clients securely into the system database
 */
export const registerUser = async (email, password) => {
  try {
    const userCredential = await createUserWithEmailAndPassword(auth, email, password);
    return userCredential.user;
  } catch (error) {
    console.error("Error creating profile account:", error.message);
    throw error;
  }
};

// Initialize the persistent global Google Provider instance
const googleProvider = new GoogleAuthProvider();

/**
 * Authenticate or register users seamlessly via a secure Google OAuth popup window
 */
export const loginWithGoogle = async () => {
  try {
    const result = await signInWithPopup(auth, googleProvider);
    return result.user;
  } catch (error) {
    console.error("Error authenticating with Google OAuth:", error.message);
    throw error;
  }
};

/**
 * Sign out the current authenticated user
 */
export const logoutUser = async () => {
  try {
    await signOut(auth);
  } catch (error) {
    console.error("Error signing out:", error.message);
    throw error;
  }
};

/* ==========================================================================
   FIRESTORE CRUD WRAPPERS (CARS COLLECTION)
   ========================================================================== */

/**
 * Real-time active listener stream for all vehicle catalog updates
 */
export const subscribeToCars = (callback) => {
  // onSnapshot fires immediately AND whenever data in the 'cars' collection updates live
  return onSnapshot(carsCollectionRef, (snapshot) => {
    const carList = snapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));
    callback(carList);
  });
};

/**
 * Fetch all car listings from Firestore (Backup REST-style lookup function)
 */
export const getAvailableCars = async () => {
  try {
    const snapshot = await getDocs(carsCollectionRef);
    return snapshot.docs.map((docItem) => ({
      ...docItem.data(),
      id: docItem.id,
    }));
  } catch (error) {
    console.error("Error fetching cars:", error);
    throw error;
  }
};

/**
 * Add a new car listing (Admin operation - Aligned name)
 */
export const addNewCar = async (carData) => {
  try {
    const docRef = await addDoc(carsCollectionRef, carData);
    return docRef.id;
  } catch (error) {
    console.error("Error adding car:", error);
    throw error;
  }
};

/**
 * Update existing car details (Admin operation - Aligned name)
 */
export const updateCarDetails = async (carId, updatedFields) => {
  try {
    const carDocRef = doc(db, "cars", carId);
    await updateDoc(carDocRef, updatedFields);
  } catch (error) {
    console.error("Error updating car:", error);
    throw error;
  }
};

/**
 * Delete a car from the catalog completely (Admin operation - Aligned name)
 */
export const deleteCarDoc = async (carId) => {
  try {
    const carDocRef = doc(db, "cars", carId);
    await deleteDoc(carDocRef);
  } catch (error) {
    console.error("Error deleting car:", error);
    throw error;
  }
};

/* ==========================================================================
   FIRESTORE CRUD WRAPPERS (RENTALS COLLECTION)
   ========================================================================== */

/**
 * SECURE: Fetch rental records ONLY matching the currently logged-in user's UID
 */
export const getUserRentals = async (userUid) => {
  try {
    // Create an isolated query to enforce data isolation levels at code layer
    const q = query(rentalsCollectionRef, where("userId", "==", userUid));
    const snapshot = await getDocs(q);
    return snapshot.docs.map((docItem) => ({
      ...docItem.data(),
      id: docItem.id,
    }));
  } catch (error) {
    console.error("Error fetching user rentals:", error);
    throw error;
  }
};

/**
 * Record a new rental transaction structure payload to database ledger
 */
export const createRental = async (rentalData) => {
  try {
    const docRef = await addDoc(rentalsCollectionRef, rentalData);
    return docRef.id;
  } catch (error) {
    console.error("Error creating rental:", error);
    throw error;
  }
};