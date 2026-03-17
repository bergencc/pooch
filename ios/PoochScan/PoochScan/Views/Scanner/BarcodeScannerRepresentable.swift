import SwiftUI
import AVFoundation
import Vision

struct BarcodeScannerRepresentable: UIViewControllerRepresentable {
    let onBarcodeFound: (String) -> Void
    
    func makeUIViewController(context: Context) -> BarcodeScannerViewController {
        let vc = BarcodeScannerViewController()
        
        vc.onBarcodeFound = onBarcodeFound
        
        return vc
    }
    
    func updateUIViewController(_ uiViewController: BarcodeScannerViewController, context: Context) {}
}
