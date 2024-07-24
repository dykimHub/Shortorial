import { Modal } from "react-bootstrap";
import styled from "styled-components";
// import { Link } from "react-router-dom";

interface ModalType {
  progress: string;
  showModal: boolean;
  path: string;
  handleCloseModal: () => void;
}

const ModalComponent = ({ progress, showModal, path, handleCloseModal }: ModalType) => {
  return (
    <Modal show={showModal} onHide={handleCloseModal}>
      <Modal.Body>
        <LoadingComponent>
          <LoadingImg src={path} />
          <Progress>{progress}</Progress>
        </LoadingComponent>
      </Modal.Body>
    </Modal>
  );
};

const LoadingComponent = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 2px solid rgba(251, 37, 118, 0.5);
  padding: 15px;
`;

const LoadingImg = styled.img`
  width: 55px;
  height: 55px;
  margin-bottom: 5px;
`;

const Progress = styled.div`
  font-size: 18px;
`;

export default ModalComponent;
