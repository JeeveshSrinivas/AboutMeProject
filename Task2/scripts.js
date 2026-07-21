let events = [
  { id: 1, name: "Project Kickoff", description: "Past meeting", date: "2025-01-15T10:00" },
  { id: 2, name: "Team Lunch", description: "Happening today!", date: "2026-07-21T12:30" },
  { id: 3, name: "Annual Conference", description: "Future event", date: "2027-09-20T09:00" }
];


let editingEventId = null;

let listView = document.getElementById("list-view");
let formView = document.getElementById("form-view");
let eventsGrid = document.getElementById("events-grid");
let eventForm = document.getElementById("eventForm");

let btnNew = document.getElementById("btn-new");
let btnCancel = document.getElementById("btn-cancel");
let btnSubmit = document.getElementById("btn-submit");

let inputName = document.getElementById("eventName");
let inputDescription = document.getElementById("eventDescription");
let inputDate = document.getElementById("eventDate");




function getDateCategory(dateString) {
  let today = new Date();
  let eventDate = new Date(dateString);

  // We set the time to 00:00:00 for both so we only compare the calendar days
  today.setHours(0, 0, 0, 0);
  eventDate.setHours(0, 0, 0, 0);

  if (eventDate < today) {
    return "card-past";    // Red CSS class
  } else if (eventDate.getTime() === today.getTime()) {
    return "card-today";   // Blue CSS class
  } else {
    return "card-future";  // Purple CSS class
  }
}

// Draws all event cards on the screen
function renderEvents() {
  // Clear the existing cards first
  eventsGrid.innerHTML = "";

  // Loop through every event in our list
  for (let i = 0; i < events.length; i++) {
    let event = events[i];

    // Create a new div element for the card
    let card = document.createElement("div");
    
    // Assign the CSS classes (e.g. "event-card card-past")
    let categoryClass = getDateCategory(event.date);
    card.className = "event-card " + categoryClass;

    // Put the event title and date inside the card
    card.innerHTML = "<h3>" + event.name + "</h3><p class='date'>" + event.date.replace("T", " ") + "</p>";

    // When this card is clicked, open the form to edit this event
    card.addEventListener("click", function() {
      openFormForEdit(event);
    });

    
    eventsGrid.appendChild(card);
  }
}

function openFormForCreate() {
  editingEventId = null;        
  eventForm.reset();             
  btnSubmit.textContent = "Add"; 
  listView.classList.add("hidden");
  formView.classList.remove("hidden");
}


function openFormForEdit(event) {
  editingEventId = event.id;     
 
  inputDescription.value = event.description;
  inputDate.value = event.date;

  btnSubmit.textContent = "Save"; 
  listView.classList.add("hidden");
  formView.classList.remove("hidden");
}

function showMainPage() {
  formView.classList.add("hidden");
  listView.classList.remove("hidden");
  eventForm.reset();
}

eventForm.addEventListener("submit", function(e) {
  e.preventDefault(); 

  if (editingEventId !== null) {
    for (let i = 0; i < events.length; i++) {
      if (events[i].id === editingEventId) {
        events[i].name = inputName.value;
        events[i].description = inputDescription.value;
        events[i].date = inputDate.value;
      }
    }
  } else {
    let newEvent = {
      id: Date.now(), 
      name: inputName.value,
      description: inputDescription.value,
      date: inputDate.value
    };
    events.push(newEvent);
  }

 
  renderEvents();
  showMainPage();
});
btnNew.addEventListener("click", openFormForCreate);
btnCancel.addEventListener("click", showMainPage);
renderEvents();