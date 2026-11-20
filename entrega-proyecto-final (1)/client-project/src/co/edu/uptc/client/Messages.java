package co.edu.uptc.client;

import java.util.Map;
import co.edu.uptc.model.Reservation;
import co.edu.uptc.model.ReservationStatus;
import co.edu.uptc.model.TimeSlot;

public final class Messages {

    private static final Map<String, String> ERRORS = Map.ofEntries(
            Map.entry("USERNAME_TAKEN", "El usuario ya existe."),
            Map.entry("DOCUMENT_TAKEN", "El documento ya está registrado."),
            Map.entry("INVALID_CREDENTIALS", "Usuario o contraseña incorrectos."),
            Map.entry("COURT_HAS_ACTIVE_RESERVATIONS",
                    "No se puede desactivar una cancha con reservas activas."),
            Map.entry("COURT_INACTIVE", "La cancha está desactivada."),
            Map.entry("COURT_NOT_FOUND", "No se encontró la cancha."),
            Map.entry("SLOT_OCCUPIED", "El horario ya está ocupado."),
            Map.entry("SLOT_AVAILABLE",
                    "El horario está disponible; puedes reservarlo directamente."),
            Map.entry("SLOT_IN_PAST", "Solo se pueden reservar horarios futuros."),
            Map.entry("SLOT_TOO_FAR", "Solo se puede reservar con máximo 30 días de anticipación."),
            Map.entry("INVALID_HOUR", "El horario debe estar entre las 6:00 a. m. y las 10:00 p. m."),
            Map.entry("RESERVATION_NOT_FOUND", "No se encontró la reserva."),
            Map.entry("CANNOT_CANCEL",
                    "Solo puedes cancelar reservas activas hasta 2 horas antes de su inicio."),
            Map.entry("OWN_RESERVATION", "Ya tienes reservado este horario."),
            Map.entry("ALREADY_WAITING", "Ya estás en la lista de espera de este horario."),
            Map.entry("NOT_AUTHORIZED", "No tienes permiso para realizar esta operación."),
            Map.entry("CONNECTION_ERROR", "No se pudo comunicar con el servidor."),
            Map.entry("EMPTY_FIELDS", "Completa todos los campos."),
            Map.entry("INVALID_NUMBER", "Capacidad y tarifa deben ser números mayores que cero."),
            Map.entry("NO_SELECTION", "Selecciona una fila de la tabla primero."),
            Map.entry("SAVE_FAILED", "No se pudo guardar la información en el servidor."));

    private static final String UNEXPECTED_ERROR = "Ocurrió un error inesperado.";

    private Messages() {
    }

    public static String error(String code) {
        return ERRORS.getOrDefault(code, UNEXPECTED_ERROR);
    }

    public static String status(ReservationStatus status) {
        switch (status) {
            case ACTIVE:
                return "Activa";
            case CANCELLED:
                return "Cancelada";
            default:
                return "Finalizada";
        }
    }

    public static String hourRange(int startHour) {
        return String.format("%02d:00 - %02d:00", startHour, startHour + 1);
    }

    public static String describe(Reservation reservation) {
        TimeSlot slot = reservation.getTimeSlot();
        return slot.getCourt().getName() + ", " + slot.getDate() + ", "
                + hourRange(slot.getStartHour());
    }
}
