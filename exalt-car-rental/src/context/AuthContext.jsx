import React, { createContext, useContext, useState, useEffect } from "react";
import { onAuthStateChanged } from "firebase/auth";
import { 
  auth, 
  loginUser as serviceLoginUser, 
  registerUser as serviceRegisterUser, 
  loginWithGoogle as serviceLoginWithGoogle, 
  logoutUser as serviceLogoutUser 
} from "../services/firebaseConfig";

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Derived safety boolean for Admin validation check matching the required identity hash
  const isAdmin = currentUser?.uid === 'X5T8Ll6eWoQ4j1wpsFltln0zjlh1';

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      setCurrentUser(user);
      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  const loginUser = async (email, password) => {
    return await serviceLoginUser(email, password);
  };

  const registerUser = async (email, password) => {
    return await serviceRegisterUser(email, password);
  };

  const loginWithGoogle = async () => {
    return await serviceLoginWithGoogle();
  };

  const logoutUser = async () => {
    return await serviceLogoutUser();
  };

  const value = {
    currentUser,
    loading,
    isAdmin,
    loginUser,
    registerUser,
    loginWithGoogle,
    logoutUser
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};