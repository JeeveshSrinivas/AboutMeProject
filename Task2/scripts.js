let events = [];
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
  const todayStart = new Date();
  todayStart.setHours(0, 0, 0, 0);

  const eventStart = new Date(dateString);
  eventStart.setHours(0, 0, 0, 0);

  if (eventDate < todayStart) {
    return "card-past";    // Red CSS class
  }

  else if (eventStart.getTime() === todayStart.getTime()) {
    return "card-today";   // Blue CSS class
  }

  return "card-future";  // Purple CSS class
}

// Draws all event cards on the screen
function renderEvents() {
  // Clear the existing cards first
  eventsGrid.innerHTML = "";

  if (events.length === 0) {
    eventsGrid.innerHTML = "<p style='grid-column: 1/-1; color: #64748b;'>No events scheduled yet. Click '+ New' to create one!</p>";
    return;
  }

  // 2. Use .map() to transform each event object into an HTML string, then .join("") into one string
  eventsGrid.innerHTML = events.map(event => {
    const categoryClass = getDateCategory(event.date);

    // Format date string for clean display
    const formattedDate = new Date(event.date).toLocaleString([], {
      dateStyle: "medium",
      timeStyle: "short"
    });

    // Return the HTML card template for this event
    return `
      <div class="event-card ${categoryClass}" data-id="${event.id}">
        <h3>${event.name}</h3>
        <p class="date">${formattedDate}</p>
      </div>
    `;
  }).join("");
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

  inputName.value = event.name;
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

  renderEvents();
}



eventForm.addEventListener("submit", function (e) {
  e.preventDefault();

  if (editingEventId !== null) {
    const index = events.findIndex(evt => evt.id === editingEventId);
    if (index !== -1) {
      events[index] = {
        id: editingEventId,
        name: inputName.value,
        description: inputDescription.value,
        date: inputDate.value
      };
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
  showMainPage();
});
btnNew.addEventListener("click", openFormForCreate);
btnCancel.addEventListener("click", showMainPage);
showMainPage();

// Click handler for ALL cards using Event Delegation
eventsGrid.addEventListener("click", function(e) {
  const card = e.target.closest(".event-card");

  if (card) {
    const cardId = Number(card.dataset.id);
    const selectedEvent = events.find(evt => evt.id === cardId);
    
    if (selectedEvent) {
      openFormForEdit(selectedEvent);
    }
  }
});