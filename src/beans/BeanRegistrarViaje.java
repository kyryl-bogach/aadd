package beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import controller.Controlador;
import model.Viaje;

@ManagedBean(name = "beanRegistrarViaje")
@SessionScoped
public class BeanRegistrarViaje implements Serializable {
	private String plazas;
	private String precio;
	public int idViaje;

	@ManagedProperty(value = "#{beanRegistrarParadaOrigen}")
	private BeanRegistrarParadaViaje origen;

	@ManagedProperty(value = "#{beanRegistrarParadaDestino}")
	private BeanRegistrarParadaViaje destino;

	public String getPlazas() {
		return plazas;
	}

	public void setPlazas(String plazas) {
		this.plazas = plazas;
	}

	public String getPrecio() {
		return precio;
	}

	public void setPrecio(String precio) {
		this.precio = precio;
	}

	public int getIdViaje() {
		return idViaje;
	}

	public void setIdViaje(int idViaje) {
		this.idViaje = idViaje;
	}

	public BeanRegistrarParadaViaje getOrigen() {
		return origen;
	}

	public void setOrigen(BeanRegistrarParadaViaje origen) {
		this.origen = origen;
	}

	public BeanRegistrarParadaViaje getDestino() {
		return destino;
	}

	public void setDestino(BeanRegistrarParadaViaje destino) {
		this.destino = destino;
	}

	/* M�todo que se encarga del registro del viaje */
	public String registrarViaje() {
		Controlador controlador = Controlador.getInstance();
		int plazasEntero;
		double precioViaje;
		Viaje viaje;
		try {
			plazasEntero = Integer.parseInt(plazas);
			precioViaje = Double.parseDouble(precio);
		} catch (NumberFormatException e) {
			return "faceletsFallo";
		}
		viaje = controlador.registrarViaje(plazasEntero, precioViaje);
		if (viaje != null) {
			idViaje = viaje.getId();
			origen.registrarParada(idViaje, true);
			destino.registrarParada(idViaje, false);
			return "faceletsWelcome";
		}
		return "faceletsFallo";
	}

}
