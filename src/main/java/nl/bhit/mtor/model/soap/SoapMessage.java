package nl.bhit.mtor.model.soap;

import java.io.Serializable;

import nl.bhit.mtor.model.Status;

/**
 * this SoapMessage exposes the method of the Message which are needed in the SOAP interface.
 * 
 * @deprecated use the ClientMessage
 * @author tibi
 */
// TODO (tibi) use the ClientMessage instead
@Deprecated
public class SoapMessage implements Serializable {
    private static final long serialVersionUID = -29873897872583055L;
    private String content;
    private Status status;
    private Long projectId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
