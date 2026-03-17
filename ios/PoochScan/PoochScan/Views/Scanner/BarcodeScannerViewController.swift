import SwiftUI
import AVFoundation
import Vision

class BarcodeScannerViewController: UIViewController, AVCaptureVideoDataOutputSampleBufferDelegate {
    var onBarcodeFound: ((String) -> Void)?

    private var captureSession: AVCaptureSession?
    private var previewLayer: AVCaptureVideoPreviewLayer?
    private var lastBarcodeTime = Date.distantPast
    private let throttleInterval: TimeInterval = 3.0
    private var isProcessing = false

    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupCamera()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
        previewLayer?.frame = view.bounds
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            self?.captureSession?.startRunning()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        captureSession?.stopRunning()
    }

    private func setupCamera() {
        let session = AVCaptureSession()
        
        session.sessionPreset = .hd1280x720

        guard let device = AVCaptureDevice.default(for: .video),
              let input = try? AVCaptureDeviceInput(device: device) else {
            showCameraPermissionAlert()
            
            return
        }

        guard session.canAddInput(input) else { return }
        session.addInput(input)

        let output = AVCaptureVideoDataOutput()
        output.setSampleBufferDelegate(self, queue: DispatchQueue(label: "cameraQueue"))
        guard session.canAddOutput(output) else { return }
        session.addOutput(output)

        let preview = AVCaptureVideoPreviewLayer(session: session)
        preview.videoGravity = .resizeAspectFill
        preview.frame = view.bounds
        view.layer.addSublayer(preview)

        self.captureSession = session
        self.previewLayer = preview

        DispatchQueue.global(qos: .userInitiated).async {
            session.startRunning()
        }
    }

    private func showCameraPermissionAlert() {
        DispatchQueue.main.async {
            let alert = UIAlertController(
                title: "Camera Access Required",
                message: "Please enable camera access in Settings to scan barcodes.",
                preferredStyle: .alert
            )
            
            alert.addAction(UIAlertAction(title: "Open Settings", style: .default) { _ in
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(url)
                }
            })
            
            alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
            self.present(alert, animated: true)
        }
    }

    // MARK: - Vision Barcode Detection
    func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
        guard !isProcessing,
              Date().timeIntervalSince(lastBarcodeTime) > throttleInterval,
              let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else { return }

        isProcessing = true

        let request = VNDetectBarcodesRequest { [weak self] req, error in
            defer { self?.isProcessing = false }
            guard let results = req.results as? [VNBarcodeObservation],
                  let barcode = results.first?.payloadStringValue,
                  !barcode.isEmpty else { return }

            self?.lastBarcodeTime = Date()
            DispatchQueue.main.async {
                self?.onBarcodeFound?(barcode)
            }
        }

        // Support all common barcode formats
        request.symbologies = [.ean13, .ean8, .upce, .code128, .code39, .qr, .pdf417, .aztec, .dataMatrix]

        let handler = VNImageRequestHandler(cvPixelBuffer: pixelBuffer, orientation: .right)
        try? handler.perform([request])
    }
}
