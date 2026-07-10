const waitItems = document.querySelectorAll(".wait-item");
const progressTitle = document.querySelector("#progress-title");
const statusPill = document.querySelector(".status-pill");
const progressFill = document.querySelector(".progress-fill");
const lookupForm = document.querySelector(".code-lookup");
const trackingInput = document.querySelector("#tracking-code");
const lookupMessage = document.querySelector(".lookup-message");
const currentCode = document.querySelector(".current-code");
const steps = document.querySelectorAll(".steps li");

const requests = {
    "RN-4821": {
        title: "Hora a medico especialista",
        state: "En preparacion",
        progress: 68,
        step: 2
    },
    "RN-3057": {
        title: "Hora a examenes",
        state: "Revision pendiente",
        progress: 42,
        step: 1
    },
    "RN-9184": {
        title: "Interconsulta",
        state: "Solicitud recibida",
        progress: 25,
        step: 0
    }
};

function showRequest(code, request) {
    progressTitle.textContent = request.title;
    statusPill.textContent = request.state;
    progressFill.style.width = `${request.progress}%`;
    progressFill.setAttribute("aria-valuenow", request.progress);
    currentCode.textContent = code;

    steps.forEach((step, index) => {
        step.classList.remove("done", "active");
        const indicator = step.querySelector(".step-indicator");
        if (indicator) {
            indicator.classList.remove("bg-teal", "border-teal", "bg-white");
            indicator.classList.add("border-secondary-subtle");
        }
        
        if (index < request.step) {
            step.classList.add("done");
            if (indicator) {
                indicator.classList.add("bg-teal", "border-teal");
                indicator.classList.remove("border-secondary-subtle");
            }
        } else if (index === request.step) {
            step.classList.add("active");
            if (indicator) {
                indicator.classList.add("bg-teal", "border-teal");
                indicator.classList.remove("border-secondary-subtle");
            }
        }
    });

    waitItems.forEach((item) => {
        item.classList.toggle("selected", item.dataset.code === code);
    });
}

waitItems.forEach((item) => {
    item.addEventListener("click", () => {
        const code = item.dataset.code;
        const request = requests[code];

        if (!request) {
            return;
        }

        trackingInput.value = code;
        lookupMessage.textContent = `Solicitud encontrada para el codigo ${code}.`;
        lookupMessage.className = "lookup-message success small mt-2 mb-0";
        showRequest(code, request);
    });
});

lookupForm?.addEventListener("submit", (event) => {
    event.preventDefault();

    const code = trackingInput.value.trim().toUpperCase();
    const request = requests[code];

    if (!code) {
        lookupMessage.textContent = "Ingresa el codigo entregado por el centro de salud.";
        lookupMessage.className = "lookup-message error small mt-2 mb-0";
        trackingInput.focus();
        return;
    }

    if (!request) {
        lookupMessage.textContent = "No encontramos una solicitud asociada a ese codigo.";
        lookupMessage.className = "lookup-message error small mt-2 mb-0";
        return;
    }

    trackingInput.value = code;
    lookupMessage.textContent = `Solicitud encontrada para el codigo ${code}.`;
    lookupMessage.className = "lookup-message success small mt-2 mb-0";
    showRequest(code, request);
});

document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            e.preventDefault();
            target.scrollIntoView({ behavior: 'smooth', block: 'start' });
            
            const navbarCollapse = document.querySelector('.navbar-collapse');
            if (navbarCollapse.classList.contains('show')) {
                const bsCollapse = new bootstrap.Collapse(navbarCollapse, { toggle: true });
            }
        }
    });
});
