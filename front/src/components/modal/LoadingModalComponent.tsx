import { Modal } from "react-bootstrap";
import styled from "styled-components";

interface ModalType {
  progress: string;
  showModal: boolean;
  path: string;
  handleCloseModal: () => void;
}

const ModalComponent = ({ progress, showModal, path, handleCloseModal }: ModalType) => {
  return (
    <StyledModal show={showModal} onHide={handleCloseModal}>
      <Modal.Body as={StyledModalBody}>
        <LoadingImg src={path} />
        <Progress>{progress}</Progress>
      </Modal.Body>
    </StyledModal>
  );
};

const StyledModal = styled(Modal)`
  .modal-content {
    background: transparent !important;
    box-shadow: none !important;
    border: 0;
    max-width: 60%;
    margin: auto;
  }
`;

const StyledModalBody = styled(Modal.Body)`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.5) !important;
  backdrop-filter: blur(10px) !important;
  border-radius: 12px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.15);
`;

const LoadingImg = styled.img`
  width: 30px;
  height: 30px;
`;

const Progress = styled.div`
  font-size: 20px;
  font-weight: 500;
  color: #333;
`;

export default ModalComponent;
