import { db } from "../firebase";
import { 
  collection, 
  getDocs, 
  addDoc, 
  doc, 
  updateDoc 
} from "firebase/firestore";

// Reference to the 'events' collection in Firestore
const eventsCollectionRef = collection(db, "events");

// 1. Fetch all events from Firestore
export const getEvents = async () => {
  const querySnapshot = await getDocs(eventsCollectionRef);
  return querySnapshot.docs.map((docSnap) => ({
    id: docSnap.id,
    ...docSnap.data()
  }));
};

// 2. Add a new event to Firestore
export const createEvent = async (newEventData) => {
  const docRef = await addDoc(eventsCollectionRef, newEventData);
  return { id: docRef.id, ...newEventData };
};

// 3. Update an existing event in Firestore
export const updateEvent = async (id, updatedData) => {
  const eventDocRef = doc(db, "events", id);
  await updateDoc(eventDocRef, updatedData);
};