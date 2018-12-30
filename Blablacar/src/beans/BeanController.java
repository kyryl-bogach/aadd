package beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.jms.TopicSubscriber;

import controller.Controlador;

@ManagedBean(name = "beanController")
@SessionScoped
public class BeanController implements Serializable {
	private static final long serialVersionUID = 1L;

	private Controlador controlador;

	private TopicSubscriber suscriptorBuzonSugerencias;

	@PostConstruct
	public void init() {
		this.controlador = new Controlador();
	}

	public Controlador getControlador() {
		return controlador;
	}

	public void setControlador(Controlador controlador) {
		this.controlador = controlador;
	}

	public TopicSubscriber getSuscriptorBuzonSugerencias() {
		return suscriptorBuzonSugerencias;
	}

	public void setSuscriptorBuzonSugerencias(TopicSubscriber suscriptorBuzonSugerencias) {
		this.suscriptorBuzonSugerencias = suscriptorBuzonSugerencias;
	}

}
