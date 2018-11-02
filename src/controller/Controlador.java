package controller;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

import dao.CocheDAO;

import dao.FactoriaDAO;
import dao.ParadaDAO;
import dao.ReservaDAO;
import dao.UsuarioDAO;
import dao.ViajeDAO;
import model.Coche;
import model.Parada;
import model.Reserva;
import model.Usuario;
import model.Valoracion;
import model.Viaje;

public class Controlador {

	private static Controlador unicaInstancia = null;
	private Usuario usuarioLogeado = null;
	public static String FECHA_SISTEMA = "26/02/2018";

	private Controlador() {
	}

	/* Patr�n Singleton */
	public static Controlador getInstance() {
		if (unicaInstancia == null) {
			unicaInstancia = new Controlador();
		}
		return unicaInstancia;
	}

	/*
	 * Este m�todo recupera el objeto usuario dado el nombre de usuario o nulo
	 * en otro caso
	 */
	public Usuario findUsuario(String usuario) {
		UsuarioDAO daoUsuario = FactoriaDAO.getInstancia().getUsuarioDAO();
		return daoUsuario.findUsuario(usuario);
	}

	/*
	 * Este m�todo recupera el objeto viaje dado el id del viaje o nulo en otro
	 * caso
	 */
	public Viaje findViaje(int id) {
		ViajeDAO daoViaje = FactoriaDAO.getInstancia().getViajeDAO();
		return daoViaje.findViaje(id);
	}

	/*
	 * Este m�todo recupera el objeto coche dado la matricula del coche o nulo
	 * en otro caso
	 */
	public Coche findCoche(String matricula) {
		CocheDAO daoCoche = FactoriaDAO.getInstancia().getCocheDAO();
		return daoCoche.findCoche(matricula);
	}

	/*
	 * Este m�todo persiste un usuario y devuelve el objeto usuario que se ha
	 * persistido o nulo en otro caso
	 */
	public Usuario registrarUsuario(String usuario, String password, Date fechaNacimiento, String profesion,
			String email, String nombre, String apellidos) {

		if (findUsuario(usuario) != null)
			return null;

		UsuarioDAO dao = FactoriaDAO.getInstancia().getUsuarioDAO();
		return dao.createUsuario(usuario, password, fechaNacimiento, profesion, email, nombre, apellidos);
	}

	/*
	 * Este m�todo permite a un usuario poder loguearse. Devuelve true en el
	 * caso de que se haya logueado false en otro caso
	 */
	public boolean loginUsuario(String usuario, String password) {

		Usuario u = findUsuario(usuario);

		if (u != null && u.getPassword().equals(password)) {
			usuarioLogeado = u;
			return true;
		}
		return false;
	}

	/*
	 * Este m�todo permite a un usuario logueado poder registrar su coche en la
	 * aplicaci�n. Devuelve true si se ha conseguido registrar su coche, false
	 * en otro caso
	 */
	public boolean addCoche(String matricula, String modelo, int year, int confort) {

		if (findCoche(matricula) != null)
			return false;

		CocheDAO daoCoche = FactoriaDAO.getInstancia().getCocheDAO();
		UsuarioDAO daoUsuario = FactoriaDAO.getInstancia().getUsuarioDAO();

		Coche coche = daoCoche.createCoche(matricula, modelo, year, confort);
		if (coche == null) {
			return false;
		}

		coche.setUsuario(this.usuarioLogeado);
		daoCoche.update();

		this.usuarioLogeado.setCoche(coche);
		daoUsuario.update();

		return true;
	}

	/*
	 * Este m�todo persiste un viaje y devuelve el objeto viaje que se ha
	 * persistido o nulo en otro caso
	 */
	public Viaje registrarViaje(int plazas, double precio) {

		if (!this.usuarioLogeado.tieneCoche()) {
			return null;
		}

		CocheDAO daoCoche = FactoriaDAO.getInstancia().getCocheDAO();
		ViajeDAO daoViaje = FactoriaDAO.getInstancia().getViajeDAO();
		Viaje viaje = daoViaje.createViaje(plazas, precio);

		this.usuarioLogeado.registrarViaje(viaje);

		daoViaje.update();
		daoCoche.update(this.usuarioLogeado.getCoche());

		return viaje;
	}

	/*
	 * Este m�todo permite indicar la parada origen de un viaje dado. Si no se
	 * encuentra el idViaje se devuelve una Parada nula
	 */
	public Parada registrarParadaOrigen(int idViaje, String ciudad, String calle, int CP, Date fecha) {
		CocheDAO daoCoche = FactoriaDAO.getInstancia().getCocheDAO();
		ParadaDAO daoParada = FactoriaDAO.getInstancia().getParadaDAO();
		ViajeDAO daoViaje = FactoriaDAO.getInstancia().getViajeDAO();

		Viaje viaje = daoViaje.findViaje(idViaje);
		if (viaje == null) {
			return null;
		}

		Parada paradaOrigen = daoParada.createParada(ciudad, calle, CP, fecha);

		viaje.setOrigen(paradaOrigen);
		daoViaje.update();
		daoCoche.update();

		return paradaOrigen;
	}

	/*
	 * Este m�todo permite indicar la parada origen de un viaje dado. Si no se
	 * encuentra el idViaje se devuelve una Parada nula
	 */
	public Parada registrarParadaDestino(int idViaje, String ciudad, String calle, int CP, Date fecha) {
		CocheDAO daoCoche = FactoriaDAO.getInstancia().getCocheDAO();
		ParadaDAO daoParada = FactoriaDAO.getInstancia().getParadaDAO();
		ViajeDAO daoViaje = FactoriaDAO.getInstancia().getViajeDAO();

		Viaje viaje = daoViaje.findViaje(idViaje);
		if (viaje == null) {
			return null;
		}

		Parada paradaDestino = daoParada.createParada(ciudad, calle, CP, fecha);

		viaje.setDestino(paradaDestino);
		daoViaje.update();
		daoCoche.update();

		return paradaDestino;
	}

	/*
	 * Este m�todo permite crear una reserva para un viaje existente. Devuelve
	 * true si se ha hecho la reserva; false en otro caso.
	 */
	public boolean reservarViaje(int idViaje, String comentario) {
		ReservaDAO daoReserva = FactoriaDAO.getInstancia().getReservaDAO();
		UsuarioDAO daoUsuario = FactoriaDAO.getInstancia().getUsuarioDAO();
		ViajeDAO daoViaje = FactoriaDAO.getInstancia().getViajeDAO();

		Viaje viaje = daoViaje.findViaje(idViaje);
		if (viaje == null) {
			return false;
		}

		if (viaje.isConductor(this.usuarioLogeado)) {
			return false;
		}

		Reserva reserva = daoReserva.createReserva(comentario);
		reserva.setUsuario(this.usuarioLogeado);
		this.usuarioLogeado.addReserva(reserva);

		reserva.setViaje(viaje);
		viaje.addReserva(reserva);

		daoReserva.update();
		daoUsuario.update();
		daoViaje.update();

		return true;

	}

	/*
	 * Este m�todo permite aceptar la reserva de viaje de un usuario. Devuelve
	 * verdadero si se ha conseguido aceptar la reserva; falso en otro caso
	 */
	public boolean aceptarViaje(int idViaje, String usuarioReservador) {
		ViajeDAO daoViaje = FactoriaDAO.getInstancia().getViajeDAO();
		ReservaDAO daoReserva = FactoriaDAO.getInstancia().getReservaDAO();

		Viaje viaje = daoViaje.findViaje(idViaje);
		if (viaje == null) {
			return false;
		}

		Reserva reserva = viaje.getReservaUsuario(usuarioReservador);
		if (reserva == null) {
			return false;
		}

		reserva.setEstadoAceptado();
		daoReserva.update(reserva);

		return true;
	}
	/*
	 * Este m�todo permite rechazar la reserva de viaje de un usuario. Devuelve
	 * verdadero si se ha conseguido rechazar la reserva; falso en otro caso
	 */
	public boolean rechazarViaje(int idViaje, String usuarioReservador) {
		ViajeDAO daoViaje = FactoriaDAO.getInstancia().getViajeDAO();
		ReservaDAO daoReserva = FactoriaDAO.getInstancia().getReservaDAO();

		Viaje viaje = daoViaje.findViaje(idViaje);
		if (viaje == null) {
			return false;
		}

		Reserva reserva = viaje.getReservaUsuario(usuarioReservador);
		if (reserva == null) {
			return false;
		}

		reserva.setEstadoRechazado();
		daoReserva.update(reserva);

		return true;
	}

	public boolean valorarViajeConductor(int idViaje, String pasajero, String comentario, int puntuacion) {
		ViajeDAO daoViaje = FactoriaDAO.getInstancia().getViajeDAO();
		UsuarioDAO daoUsuario = FactoriaDAO.getInstancia().getUsuarioDAO();

		// ReservaDAO daoReserva = FactoriaDAO.getInstancia().getReservaDAO();

		Viaje viaje = daoViaje.findViaje(idViaje);
		if (viaje == null) {
			return false;
		}

		Usuario usuarioPasajero = daoUsuario.findUsuario(pasajero);
		if (usuarioPasajero == null) {
			return false;
		}

		Reserva reserva = viaje.getReservaUsuario(pasajero);
		if (reserva == null) {
			return false;
		}

		Valoracion valoracion = new Valoracion(comentario, puntuacion);
		valoracion.setEmisor(this.usuarioLogeado);
		valoracion.setReceptor(usuarioPasajero);
		valoracion.setReserva(reserva);
		usuarioPasajero.addValoracion(valoracion);
		this.usuarioLogeado.addValoracion(valoracion);
		reserva.addValoracion(valoracion);

		/*
		 * daoReserva.update(reserva); daoUsuario.update(); daoViaje.update();
		 */

		return true;

	}

	public boolean valorarViajePasajero(int idViaje, String conductor, String comentario, int puntuacion) {
		ViajeDAO daoViaje = FactoriaDAO.getInstancia().getViajeDAO();
		UsuarioDAO daoUsuario = FactoriaDAO.getInstancia().getUsuarioDAO();

		// ReservaDAO daoReserva = FactoriaDAO.getInstancia().getReservaDAO();

		Viaje viaje = daoViaje.findViaje(idViaje);
		if (viaje == null) {
			return false;
		}

		Usuario usuarioConductor = daoUsuario.findUsuario(conductor);
		if (usuarioConductor == null) {
			return false;
		}

		Reserva reserva = viaje.getReservaUsuario(this.usuarioLogeado.getUsuario());
		if (reserva == null) {
			return false;
		}

		Valoracion valoracion = new Valoracion(comentario, puntuacion);
		valoracion.setEmisor(this.usuarioLogeado);
		valoracion.setReceptor(usuarioConductor);
		valoracion.setReserva(reserva);
		usuarioConductor.addValoracion(valoracion);
		this.usuarioLogeado.addValoracion(valoracion);
		reserva.addValoracion(valoracion);

		/*
		 * daoReserva.update(reserva); daoUsuario.update(); daoViaje.update();
		 */

		return true;

	}

	public Collection<Viaje> listarViajes(boolean pendientes, boolean realizados, boolean propios, boolean ordenFecha,
			boolean ordenCiudad) {

		ViajeDAO daoViaje = FactoriaDAO.getInstancia().getViajeDAO();
		if (pendientes || realizados || propios || ordenFecha || ordenCiudad) {
			return daoViaje.getAllViajesBy(pendientes, realizados, propios, ordenFecha, ordenCiudad);
		}

		return daoViaje.getAllViajes();

	}

	public static Date fromStringToDate(String fecha) {
		SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
		Date date = null;

		try {
			date = formatoDelTexto.parse(fecha);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;

	}

	public Usuario getUsuarioLogeado() {
		return this.usuarioLogeado;
	}

}
