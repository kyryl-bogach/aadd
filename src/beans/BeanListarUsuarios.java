package beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import controller.Controlador;
import model.Usuario;

@ManagedBean(name = "beanListarUsuarios")
@RequestScoped
public class BeanListarUsuarios implements Serializable {
	private static final long serialVersionUID = 1L;
	private Collection<Usuario> usuarios;
	
	public Collection<Usuario> getUsuarios() {
		usuarios = new LinkedList<Usuario>();
		Collection<Usuario> todosUsuarios = Controlador.getInstance().getAllUsuarios();
		for (Usuario usuario : todosUsuarios) {
			usuarios.add(usuario);
		}
		return usuarios;
	}

	public BeanListarUsuarios() {
		usuarios = new LinkedList<Usuario>();
	}

	public void setUsuarios(Collection<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
}