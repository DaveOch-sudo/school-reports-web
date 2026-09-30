import { useEffect, useRef } from "react";
import type { ReactNode, MouseEvent } from "react";

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: ReactNode;
  className?: string;
}
function Modal({
  isOpen,
  onClose,
  title,
  children,
  className = "",
}: ModalProps) {
  const dialogRef = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    const dialog = dialogRef.current;

    if (!dialog) return;

    if (isOpen && !dialog.open) {
      dialog.showModal();
    } else if (!isOpen && dialog.open) {
      dialog.close();
    }
  }, [isOpen]);

  function handleBackdropClick(event: MouseEvent<HTMLDialogElement>) {
    const dialog = event.currentTarget;
    const rect = dialog.getBoundingClientRect();

    const clickedOutside =
      event.clientX < rect.left ||
      event.clientX > rect.right ||
      event.clientY > rect.bottom ||
      event.clientY < rect.top;

    if (clickedOutside) {
      onClose();
    }
  }
  return (
    <>
      <dialog
        ref={dialogRef}
        onCancel={(event) => {
          event.preventDefault();
          onClose();
        }}
        onClick={handleBackdropClick}
        aria-labelledby="modal-title"
        className={`
        m-auto w-[calc(100%-2rem)] max-w-lg
        rounded-xl bg-white p-0 text-gray-900
        shadow-xl backdrop:bg-black/50
        ${className}
      `}
      >
        <div className="flex items-center justify-between border-b border-gray-200 px-6 py-4">
          <h2 id="modal-title" className="text-lg font-semibold">
            {title}
          </h2>

          <button
            type="button"
            onClick={onClose}
            aria-label="Close dialog"
            className="rounded-md p-2 text-gray-500 transition hover:bg-gray-100 hover:text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            &times;
          </button>
        </div>

        <div className="p-6">{children}</div>
      </dialog>
    </>
  );
}

export default Modal;
